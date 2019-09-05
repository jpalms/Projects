### Download the dataset
* Download the ["Amazon fine food reviews"](https://www.kaggle.com/snap/amazon-fine-food-reviews/downloads/amazon-fine-food-reviews.zip/2) dataset
* Extract a file "Reviews.csv" into a folder called "amazon-fine-food-reviews" so that all reviews are in this path 
```
amazon-fine-food-reviews/Reviews.csv
``` 

### Build this example in this folder
```
../gradlew build
```
or on Windows machine
```
cd ..
start gradlew.bat build
```


### Run this example in this folder
```
java -cp build/libs/basic_word_count-1.0-SNAPSHOT.jar edu.rit.cs.basic_word_count.WordCount_Seq
```

### Issues

- “Exception in thread “main” java.lang.OutOfMemoryError: Java heap space” error. One naive solution is increase the memory. But, you need to think about how to optimize the program, so that you can avoid this naive fix.
```
export JVM_ARGS="-Xmx1024m -XX:MaxPermSize=256m"
```