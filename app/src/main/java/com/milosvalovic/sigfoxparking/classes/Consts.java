package com.milosvalovic.sigfoxparking.classes;

import okhttp3.logging.HttpLoggingInterceptor;

public final class Consts {

    /** Global for this project**/
    public static final String URL = "https://valovic.studenthosting.sk/";
    public static final String WEB_URL = "";
    public static final String API_URL = URL + "api/";
    public static final String DATA_URL = URL + "data/";
    public static final String INVOICE_URL = DATA_URL + "invoice/";
    public static final String PRODUCT_PHOTO_URL = DATA_URL + "product_photo/";
    public static final String PARTNER_PHOTO_URL = DATA_URL + "partner_photo/";
    public static final String PARTNER_COVER_PHOTO_URL = DATA_URL + "partner_cover_photo/";
    public static final String CATEGORY_PHOTO_URL = DATA_URL + "product_category_image/";
    public static final String APP_TAG = "FTM";
    public static final String SD_CARD_FOLDER_NAME = "Fitsmapp";
    public static final String PROJECT_NAME = "fitsmapp";
    public static final String SHARED_PREFERENCES = "Fitsmapp";
    public static final String IMAGE_DIRECTORY = "/Fitsmapp";

    /** API SETTINGS **/
    public static final Boolean API_LOG_CUSTOM_HEADERS = true;
    public static final Boolean API_TRUST_ALL_SLL = true;
    public static final Boolean API_FORCE_HTTP_1_PROTOCOL = true;
    public static final Boolean API_FOLLOW_REDIRECTS = true;
    public static final Boolean API_PERSISTENT_COOKIE = true;
    public static final HttpLoggingInterceptor.Level API_LOG_LEVEL = HttpLoggingInterceptor.Level.BODY;


}
