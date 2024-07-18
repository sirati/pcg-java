//#![feature(core_intrinsics)]

use std::ffi::CString;
use rand_core::{RngCore, impls};
use testu01::unif01::{Unif01Gen, Unif01Pair};

//creates struct that contains to int segments each 32mb long.
//arg 1, 2 are the starting addresses of the segments
//arg 3 is a pointer to a fn(){} callback
#[no_mangle]
pub extern "C" fn create_exchange_adapter_lib_pcg_crush(
    page_lower: *mut [u32; 64 / 2 / 4 * 1024 * 1024],
    page_upper: *mut [u32; 64 / 2 / 4 * 1024 * 1024],
    page_exhausted: extern "C" fn(x: u32) -> u32,
) -> *mut ExchangeAdapter {
    let adapter = Box::new(ExchangeAdapter {
        page_lower: unsafe {&mut *page_lower},
        page_upper: unsafe {&mut *page_upper},
        current_page: CurrentPage::Lower,
        current_index: 0,
        page_exhausted,
    });
    println!("create_exchange_adapter_lib_pcg_crush");


    Box::into_raw(adapter)
}

//function that takes *mut ExchangeAdapter and deallocates it
#[no_mangle]
pub extern "C" fn destroy_exchange_adapter_lib_pcg_crush(adapter: *mut ExchangeAdapter) {
    unsafe {
        _ = Box::from_raw(adapter);
    }
    println!("destroy_exchange_adapter_lib_pcg_crush");
}

fn write_internal_state(gen: &mut ExchangeAdapter) {
    println!(
        "currently not supporter. current_index: {}, page: {:?}", gen.current_index, gen.current_page
    )
}

#[no_mangle]
pub extern "C" fn launch_big_lib_pcg_crush(adapter: *mut ExchangeAdapter) {
    println!("launch_big_lib_pcg_crush");
    let adapter = unsafe{ Box::from_raw(adapter)};
    let mut unif01 = to_unif01(*adapter, write_internal_state);

    // Apply the small crush battery to it:
    testu01::battery::big_crush(&mut unif01);
}

#[no_mangle]
pub extern "C" fn launch_medium_lib_pcg_crush(adapter: *mut ExchangeAdapter) {
    println!("launch_medium_lib_pcg_crush");
    let adapter = unsafe{ Box::from_raw(adapter)};
    let mut unif01 = to_unif01(*adapter, write_internal_state);

    // Apply the small crush battery to it:
    testu01::battery::crush(&mut unif01);
}

#[no_mangle]
pub extern "C" fn launch_small_lib_pcg_crush(adapter: *mut ExchangeAdapter) {
    //trigger debugger this will crash the jvm if no dbg is attached
    //unsafe { std::intrinsics::breakpoint(); }


    println!("launch_small_lib_pcg_crush");
    let adapter = unsafe{ Box::from_raw(adapter)};
    let mut unif01 = to_unif01(*adapter, write_internal_state);

    // Apply the small crush battery to it:
    testu01::battery::small_crush(&mut unif01);
}

fn to_unif01<F: FnMut(&mut ExchangeAdapter)>(adapter: ExchangeAdapter, f: F) -> Unif01Gen<Unif01Pair<ExchangeAdapter, F>> {
    //print the first 16 int in hex format separated by space
    for i in 0..16 {
        print!("{:08x} ", adapter.page_lower[i]);
    }
    println!();
    let name = "java_adapter";
    let c_name = CString::new(name).unwrap();


    // Build an object than can  be converted to something that TestU01 can test:
    Unif01Gen::new(Unif01Pair(adapter, f), c_name)
}

#[derive(Debug)]
pub enum CurrentPage {
    Lower,
    Upper,
}

pub struct ExchangeAdapter {
    pub page_lower: &'static mut [u32; 64 / 2 / 4 * 1024 * 1024], //todo make this an &mut this should be fine!
    pub page_upper: &'static mut [u32; 64 / 2 / 4 * 1024 * 1024],
    pub current_page: CurrentPage,
    pub current_index: usize,
    pub page_exhausted: extern "C" fn(x: u32) -> u32,
}

impl RngCore for ExchangeAdapter {
    #[inline]
    fn next_u32(&mut self) -> u32 {
        let page = match self.current_page {
            CurrentPage::Lower => &mut self.page_lower,
            CurrentPage::Upper => &mut self.page_upper,
        };

        let value = page[self.current_index];
        self.current_index += 1;
        if self.current_index == page.len() {
            self.current_page = match self.current_page {
                CurrentPage::Lower => CurrentPage::Upper,
                CurrentPage::Upper => CurrentPage::Lower,
            };
            self.current_index = 0;
            (self.page_exhausted)(0xdeadbeef);
            /*unsafe {
                let b: u32 =
                println!("{:08x}", b);
            }*/
        }
        value
    }


   #[inline]
    fn next_u64(&mut self) -> u64 {
       impls::next_u64_via_u32(self)
    }

    #[inline]
    fn fill_bytes(&mut self, dest: &mut [u8]) {
        impls::fill_bytes_via_next(self, dest)
    }

    fn try_fill_bytes(&mut self, dest: &mut [u8]) -> Result<(), rand_core::Error> {
        self.fill_bytes(dest);
        Ok(())
    }

}

