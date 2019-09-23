### Download the dataset
* Download the ["Amazon fine food reviews"](https://www.kaggle.com/snap/amazon-fine-food-reviews/downloads/amazon-fine-food-reviews.zip/2) dataset
* Extract a file "Reviews.csv" into a folder called "amazon-fine-food-reviews" so that all reviews are in this path 
```
amazon-fine-food-reviews/Reviews.csv
``` 

### Build this example as a jar
```
mvn package
```

### Run WordCount_Seq
```
java -cp target/basic_word_count-1.0-SNAPSHOT.jar edu.rit.cs.basic_word_count.WordCount_Seq
```

### Run WordCount_Seq_Improved
```
java -cp target/basic_word_count-1.0-SNAPSHOT.jar edu.rit.cs.basic_word_count.WordCount_Seq_Improved
```

### Issues
- “Exception in thread “main” java.lang.OutOfMemoryError: Java heap space” error. One naive solution is increase the memory. But, you need to think about how to optimize the program, so that you can avoid this naive fix.
```
export JVM_ARGS="-Xmx1024m -XX:MaxPermSize=256m"
```

### Task 1
    ### WordCount_Seq_Sorted
    ```
    Created an ArrayList that uses InsertionSort to keep a sort list.
    list was used when printing to determine the order to print the map from a-z.
    ```

### Task 2
    ### WordCount_Threads
    ```
    Created sublists of the data that was passed to each thread.
    Thread used logic from WordCount_Seq_Sorted to sort each partition.
    While threads were running, main program waited for all threads to finish before merging and sorting results.
    The maps and list were merged/sorted seperately.
    ```
### Task 3
    ### WordCount_Cluster_master
        ```
        Contained all logic from WordCount_Threads that happend outside a thread.
        Instead of using Threads for the partion data, a Server is created that waits for clients.
        The clients sort the partitions and return the results.
        After all connections are establish, wait till all connections are finished than merge and sort results.

        ```
    ### WordCount_Cluster_worker
        ```
        Contained all logic from WordCount_Threads that happend inside a thread.
        Establishes a connection with the Server to get the partitioned data, then return results.
        ```