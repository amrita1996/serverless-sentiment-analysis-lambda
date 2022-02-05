package org.example;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.comprehend.AmazonComprehend;
import com.amazonaws.services.comprehend.AmazonComprehendClientBuilder;
import com.amazonaws.services.comprehend.model.DetectSentimentRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.Objects.isNull;

public class SentimentAnalysis implements RequestHandler<S3Event, String> {
    public static final String BUCKET_NAME = "twitterdatab00862800";
    S3Service s3Service = new S3Service();
    @Override
    public String handleRequest(S3Event event, Context context) {
        String fileName = "file_mongo_tweets.txt";
        String bucketName = "twitterdatab00862800";
        String fileContent = s3Service.downloadFileContent(bucketName, fileName);
        if (isNull(fileContent)) { return "Failure"; }
        final List<String> stringStream = stream(fileContent.split("\n\n"))
                .filter(s -> !s.trim().isEmpty()).collect(Collectors.toList());
        final List<TweetSentiment> sentimentResults = stringStream.stream()
                .map(this::comprehend).collect(Collectors.toList());
        try {
            writeToFile(sentimentResults);
        } catch (IOException e) {
            return "Error";
        }
        return "Success";
    }
    private TweetSentiment comprehend(String content) {
        AWSCredentialsProvider awsCreds = DefaultAWSCredentialsProviderChain.getInstance();
        AmazonComprehend comprehendClient = AmazonComprehendClientBuilder.standard().withCredentials(awsCreds)
                        .withRegion(Regions.US_EAST_1).build();
        DetectSentimentRequest detectSentimentRequest = new DetectSentimentRequest().withText(content)
                .withLanguageCode("en");
        return new TweetSentiment(content, comprehendClient.detectSentiment(detectSentimentRequest));
    }
    private void writeToFile(List<TweetSentiment> sentimentResults) throws IOException {
        File file = new File("/tmp/results.json");
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        bufferedWriter.write(String.format("{\"results\": %s}", sentimentResults.toString()));
        bufferedWriter.close();
        s3Service.putObject(BUCKET_NAME, file, "results.json");

    }
}
