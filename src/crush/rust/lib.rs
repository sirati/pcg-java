pub fn add(left: usize, right: usize) -> usize {
    left + right
}

//creates struct that contains to int segments each 32mb long.
//arg 1, 2 are the starting addresses of the segments
//arg 3 is a pointer to a fn(){} callback
#[no_mangle]
pub extern "C" fn create_exchange_adapter_lib_pcg_crush(
    page_lower: *mut [u32; 64 / 2 / 4 * 1024 * 1024],
    page_upper: *mut [u32; 64 / 2 / 4 * 1024 * 1024],
    page_exhausted: *const fn(),
) -> *mut ExchangeAdapter {
    let adapter = Box::new(ExchangeAdapter {
        pageLower: page_lower,
        pageUpper: page_upper,
        currentPage: CurrentPage::Lower,
        page_exhausted,
    });
    println!("create_exchange_adapter_lib_pcg_crush");
    Box::into_raw(adapter)
}

//function that takes *mut ExchangeAdapter and deallocates it
#[no_mangle]
pub extern "C" fn destroy_exchange_adapter_lib_pcg_crush(adapter: *mut ExchangeAdapter) {
    unsafe {
        Box::from_raw(adapter);
    }
    println!("destroy_exchange_adapter_lib_pcg_crush");
}

enum CurrentPage {
    Lower,
    Upper,
}

pub struct ExchangeAdapter {
    pub pageLower: *mut [u32; 64 / 2 / 4 * 1024 * 1024],
    pub pageUpper: *mut [u32; 64 / 2 / 4 * 1024 * 1024],
    pub currentPage: CurrentPage,
    pub page_exhausted: *const fn(),
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn it_works() {
        let result = add(2, 2);
        assert_eq!(result, 4);
    }
}
