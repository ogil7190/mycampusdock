package com.swalla.campusdock.Utils;

/**
 * Created by ogil on 14/01/18.
 */

public class Config {

    public static class Requests {
        public static final int REQ_ENROLL = 101;
        public static final int REQ_REACH_EVENT = 103;
        public static final int REQ_REACH_BULLETIN = 105;
        public static final int REQ_WITHDRAW = 107;
        public static final int REQ_LIKE = 7190;

        public static final int REQ_FETCH_EVENT = 101;
        public static final int REQ_FETCH_BULLETIN = 103;
        public static final int REQ_CHECK_EVENT_SUBSCRIPTION = 102;
    }

    public static class Prefs {
        public static final String PREF_NAME = "CampusDockProfile";
        public static final String PREF_REG_ID_KEY = "regId";
        public static final String PREF_USER_NAME = "user_name";
        public static final String PREF_USER_ROLL = "user_roll";
        public static final String PREF_USER_EMAIL = "user_email";
        public static final String PREF_USER_CLASS = "user_class";
        public static final String PREF_USER_PHONE = "user_phone";
        public static final String PREF_USER_SUBSCRIPTIONS = "user_subscriptions";
        public static final String PREF_USER_IS_LOGGED_IN = "userLoggedIn";
        public static final String PREF_USER_API_KEY = "apiKey";
        public static final String PREF_USER_LIKES_US = "likeYou";

        public static final String PREF_BACKUP_ARRAY_EVENT = "backupEventArray";
        public static final String PREF_BACKUP_ARRAY_BULLETIN = "backupBulletinArray";
        public static final String PREF_BACKUP_EVENT_FETCH_COUNT = "backupEventCount";
        public static final String PREF_BACKUP_BULLETIN_FETCH_COUNT = "backupBulletinCount";
        public static final String PREF_BACKUP_EVENT_FETCH_COUNT_LIMIT = "backupEventLimit";
        public static final String PREF_BACKUP_BULLETIN_FETCH_COUNT_LIMIT = "backupBulletinLimit";
    }

    public static class Flags {
        public static final String FLAG_DATA_FETCH = "dataFetchingStarted";
        public static final String FLAG_REG_COMPLETE = "RegistrationDone";
        public static final String FLAG_NEW_UPDATE = "newUpdate";
        public static final String FLAG_SHOW_NEW_EVENT = "newEvent";
        public static final String FLAG_SHOW_NEW_BULLETIN = "new Bulletin";
    }

    public static class Types {
        public static final String TYPE_BULLETIN = "bulletin";
        public static final String TYPE_EVENT = "event";
        public static final String TYPE_EVENT_UPDATE = "update_event";
        public static final String TYPE_VERIFICATION = "verification";
        public static final String TYPE_IMPLICIT = "implicit";
        public static final String TYPE_EXPLICIT = "explicit";
    }

    public static class Urls{
        public static final String URL_BASE_FILES = "http://www.figr.in/mgiep/public_html/uploads/" ;
        public static final String URL_BASE_FILES_BULLETIN = "http://www.figr.in/mgiep/public_html/uploads/class/" ;
    }

    public static class Event {
        public static final String STR_EVENT_ID = "event_id";
        public static final String STR_EVENT_TITLE = "event_title";
        public static final String STR_EVENT_DESCRIPTION = "event_description";
        public static final String STR_EVENT_CREATOR = "creator_name";
        public static final String STR_EVENT_TIME = "event_start_std";
        public static final String STR_EVENT_EXPIRY = "event_end_std";
        public static final String STR_EVENT_IMAGE = "event_image_url";
        public static final String STR_EVENT_TAGS = "event_tags";
    }

    public static class Bulletin {
        public static final String STR_BULLETIN_ID = "bulletin_id";
        public static final String STR_BULLETIN_TITLE = "bulletin_title";
        public static final String STR_BULLETIN_DESCRIPTION = "bulletin_description";
        public static final String STR_BULLETIN_CREATOR = "creator_name";
        public static final String STR_BULLETIN_TIME = "bulletin_time_stamp";
        public static final String STR_BULLETIN_EXPIRY = "bulletin_expiry";
        public static final String STR_BULLETIN_FILES = "bulletin_file_urls";
    }
}
