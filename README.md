# Deleted files recovery

A small utility to recover lost files of some reasonable size (like photos, but not large videos) by performing a full
scan of a disk image file. It uses the fact that many file formats contain a fixed well-defined header that, being found
anywhere in a disk image file, signifies about a start of a file of that particular format. The implementation assumes
that the lost files are up to some certain size, for example, 50 MB.

# Usage

0) Make sure you don't write to the disk with lost files.
1) Make a plain unencoded disk image file of a disk. Under Windows, you can
   use [HxD editor](https://mh-nexus.de/en/downloads.php?product=HxD20) and its "Tools -> Open disk..." and "File ->
   Save as..." features.
2) Figure out the `pattern` to search for. To do that, you can open a couple of existing files (of the format you want
   to detect) in a hex editor and find the common sequence of bytes at the beginning of each file. The length of the
   pattern should ideally be between 5 and 20 bytes. For example, for Canon CR2 format it can be:
   49,49,2A,00,10,00,00,00,43,52,02,00
3) Run the app located in bin/app.jar.  
   For that you need to have [the latest JRE](https://jdk.java.net/17/).  
   **Note**: set `discoveredFileSize` to as low value as possible that is larger than sizes of any files to be found.\
   \
   For Canon CR2 file recovery:
   ```
   path/to/jdk/bin/java.exe -jar path/to/app/build/app.jar inputFilePath=some/file.bin outputDirPath=some/dir outputFileExtension=cr2 discoveredFileSize=50000000 pattern=49,49,2A,00,10,00,00,00,43,52,02,00  
   or  
   path/to/jdk/bin/java.exe -jar path/to/app/build/app.jar inputFilePath=some/file.bin outputDirPath=some/dir outputFileExtension=cr2 discoveredFileSize=50000000 sampleFilePath=photo/sample.cr2 sampleFilePatternLength=12
   ```
   \
   Testing:
   ```
   path/to/jdk/bin/java.exe -jar path/to/app/build/app.jar inputFilePath=path/to/app/build/testfile.bin outputDirPath=path/to/app/tmp/output outputFileExtension=bin discoveredFileSize=10 pattern=01,02,03  
   (should discover 17 files)
   ```

# Constraints

- discoveredFileSize < 1 GB
- sampleFile < 1 GB
- pattern length < 50
- input file size is unconstrained

# Support

Ask the developer if you have questions or suggestions.