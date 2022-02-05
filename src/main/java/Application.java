import org.example.S3Service;

import java.io.File;

public class Application {
    public static void main(String[] args) {
        File file = new File("src/main/resources/file_mongo_tweets.txt");
        S3Service s3Service = new S3Service();
        s3Service.createBucket("twitterdatab00862800");
        s3Service.putObject("twitterdatab00862800", file, "file_mongo_tweets.txt");
    }
}
