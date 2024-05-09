package com.example.playtronic

import android.content.Context
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder
import twitter4j.Twitter

class TwitterRepository(context: Context) {
    val twitter: Twitter

    init {
        val cb = ConfigurationBuilder()
        cb.setDebugEnabled(true)
            .setOAuthConsumerKey(context.getString(R.string.com_twitter_sdk_android_CONSUMER_KEY))
            .setOAuthConsumerSecret(context.getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET))
            .setOAuthAccessToken(context.getString(R.string.com_twitter_sdk_android_ACCESS_TOKEN))
            .setOAuthAccessTokenSecret(context.getString(R.string.com_twitter_sdk_android_ACCESS_TOKEN_SECRET))

        val tf = TwitterFactory(cb.build())
        twitter = tf.instance
    }
}