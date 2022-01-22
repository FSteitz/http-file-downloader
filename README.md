# HTTP File Downloader
A small Kotlin-based CLI application for downloading files via sets of HTTP URLs. Each set can be configured via a JSON
file. This downloader even supports optional variable substitution in URLs.

Currently, only `GET` requests are supported. But it's easy to implement support for more HTTP methods. 

## Building the application
`./gradlew build`

## Running the application
This application can be run with the following command:
`./gradlew run --args='"<json-config-location>" "<target-dir>"'`

`<json-config-location>` is a placeholder for the absolute path of the folder that contains the JSON file(s) which 
contain the URLs. This project provides a sample JSON file in the folder `endpoint-configs`. The absolute path of that 
folder can be provided as an argument to get up and running quickly.

`<target-dir>` is a placeholder for the folder the downloaded files shall be saved to. Each JSON supports the declaration
of a sub folder to make the grouping of downloaded files easier. The sample JSON `shares.json` declares the sub folder 
`shares`.
