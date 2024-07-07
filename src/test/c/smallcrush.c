// #######################################
// ### smallCrush from File Automation ###
// #######################################
//
//
// author: Louisa Sommer
// date: 06.07.2023
//
// info: ressource release not working right now (TODO)
//

#include "bbattery.h"
#include <stdio.h>
#include <string.h>

#define NUM_FILES 5  // Number of input files
#define POSTFIX "_SmallCrush.txt"


void remove_extension(const char* input, char* output) {
    strcpy(output, input);
    char *dot = strrchr(output, '.');
    if (dot != NULL) {
        *dot = '\0';
    }
}

int main(void)
{
    // Hardcoded list of input files
    const char *inputFiles[NUM_FILES] = {
        "PCG_RXS_M_XS_32.txt",
        "PCG_RXS_M_XS_64.txt",
        "PCG_XSH_RR.txt",
        "PCG_XSH_RS.txt",
        "PCG_XSL_RR.txt"
    };

    char outputFileName[256];
    char baseFileName[256];

    for (int i = 0; i < NUM_FILES; i++) {
        // Remove the extension from the input file name
        remove_extension(inputFiles[i], baseFileName);

        // Create output file name
        snprintf(outputFileName, sizeof(outputFileName), "%s%s", baseFileName, POSTFIX);

        // Open a file for writing the output (creates the file if it doesn't exist)
        FILE *outputFile = fopen(outputFileName, "w");
        if (outputFile == NULL) {
            perror("fopen");
            fprintf(stderr, "Failed to open file: %s\n", outputFileName);
            fflush(stderr); // Ensure the error message is written immediately
            continue; // Skip to the next file if fopen fails
        }

        // Close the file immediately after creation
        fclose(outputFile);

        // Redirect stdout to the file
        outputFile = freopen(outputFileName, "w", stdout);
        if (outputFile == NULL) {
            perror("freopen");
            fprintf(stderr, "Failed to reopen file: %s\n", outputFileName);
            fflush(stderr); // Ensure the error message is written immediately
            return 1;
        }

        // Make a non-const copy of the input file name to pass to bbattery_SmallCrushFile
        char inputFileName[256];
        snprintf(inputFileName, sizeof(inputFileName), "%s", inputFiles[i]);

        // Log the test start
        fprintf(stderr, "Running SmallCrush on: %s, outputting to: %s\n", inputFileName, outputFileName);
        fflush(stderr); // Ensure the log message is written immediately

        // Run the SmallCrush test and check the return value
        bbattery_SmallCrushFile(inputFileName);

        // Log after SmallCrush test
        fprintf(stderr, "Finished SmallCrush on: %s\n", inputFileName);
        fflush(stderr); // Ensure the log message is written immediately

        // Close the file
        fclose(outputFile);

        // Reset stdout to console
        outputFile = freopen("/dev/tty", "a", stdout);
        if (outputFile == NULL) {
            perror("freopen");
            fprintf(stderr, "Failed to reset stdout to console\n");
            fflush(stderr); // Ensure the error message is written immediately
            return 1;
        }

        // Log the test completion
        fprintf(stderr, "Completed SmallCrush on: %s\n", inputFileName);
        fflush(stderr); // Ensure the log message is written immediately
    }

    return 0;
}
