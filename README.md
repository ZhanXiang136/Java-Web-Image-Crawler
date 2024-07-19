<h1>Java Web Image Crawler</h1>

## Functionality
- Web crawler that can find all images on the web page(s) that it crawls.
- Crawl sub-pages to find more images.
- Implemented multi-threading so that the crawl can be performed on multiple sub-pages at a time.
- Crawl within the same domain as the input URL.
- Avoid re-crawling any pages that have already been visited.

## Requirements
Before beginning, make sure you have the following installed and ready to use
- Maven 3.5 or higher
- Java 8
  - Exact version, **NOT** Java 9+ - the build will fail with a newer version of Java

## Setup
To start, open a terminal window and navigate to wherever you unzipped to the root directory `imagefinder`. To build the project, run the command:

>`mvn package`

If all goes well you should see some lines that end with "BUILD SUCCESS". When you build your project, maven should build it in the `target` directory. To clear this, you may run the command:

>`mvn clean`

To run the project, use the following command to start the server:

>`mvn clean test package jetty:run`

>`open localhost:8080`
