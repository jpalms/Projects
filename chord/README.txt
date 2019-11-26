// To run, run the following commands from the "chord" directory.

mvn package


// To Run an Anchor Node (MiniServer)

java -cp target/chord-1.0.jar edu.rit.cs.controller.AnchorNode

// To Run an individual Node (Peer)
// Note: Server must be running, and the argument for NodeCLI is the server IP

java -cp target/chord-1.0.jar edu.rit.cs.view.NodeCLI 127.0.0.1

// Follow the prompts.
// For file path, the path can be absolute or relative, but when querying, only the name must be specified.
// Example:
//      Insert:     chord/exampleFile.txt
//      Lookup:     exampleFile.txt