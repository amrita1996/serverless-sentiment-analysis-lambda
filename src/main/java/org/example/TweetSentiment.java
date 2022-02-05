package org.example;

import com.amazonaws.services.comprehend.model.DetectSentimentResult;

public class TweetSentiment {
    private String tweet;
    private DetectSentimentResult sentiment;

    public TweetSentiment(String tweet, DetectSentimentResult sentimentResult) {
        this.tweet = tweet;
        this.sentiment = sentimentResult;
    }

    @Override
    public String toString() {
        return "\n{" +
                "\n\"tweet\":\"" + tweet.replaceAll("\\s+", " ")
                .replaceAll("\n", " ")
                .replaceAll("\"", "").trim() + '\"' +
                ", \n\"sentiment\":\"" + sentiment.toString() +
                "\"}";
    }
}
