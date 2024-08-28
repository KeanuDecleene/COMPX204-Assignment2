//Name: Keanu De Cleene
//ID: 1626997

/*
 * The HttpServerRequest class handles parsing of HTTP rerquest from a client.
 * ity extracts key information being the file and the host,
 * and determines when the request process is finished.
 */
class HttpServerRequest
{
    private String file = null; //requested file path
    private String host = null; //host information
    private boolean done = false; //flag to indicate if request is finished 
    private int line = 0; //keeps track of line number being processed

    public boolean isDone() { return done; }
    public String getFile() { return file; }
    public String getHost() { return host; }



    /*
     * Processes a line from the HTTP request. This method is called for each line of request
     * extracts important information and determines when request is finished.
     * 
     * @param line a single line of the HTTP request to be processed
     */
    public void process(String line) {
        //increment the line counter each time the process method is called until end of header
        this.line++;
        //checking line is empty (indicating end of the header)
        if (line == null || line.isEmpty()) {
            done = true; //request is complete
            return; 
        }

        String[] parts = line.split(" ");

        //ensure the first line of the HTTP request is processed correctly
        if (this.line == 1 && parts.length == 3 && parts[0].compareTo("GET") == 0) {
            file = parts[1].substring(1); //extract the requested file path from the GET command
            //if the path ends with a /, default to index.html
            if (file.endsWith("/")) {
                file += "index.html";
            }
        } else if (line.startsWith("Host: ")) { //check if the line specifies the host information 
            host = line.substring(6); //extract the host from the "Host: " 
        }
    }
}

