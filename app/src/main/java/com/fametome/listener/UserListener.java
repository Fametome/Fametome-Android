package com.fametome.listener;

public interface UserListener {
    public interface onUserLoadedListener{
        public abstract void onUserLoaded();
    }
    public interface onFacesLoadedListener{
        public abstract void onFacesLoaded();
    }
    public interface onFriendsLoadedListener{
        public abstract void onFriendsLoaded();
        public abstract void onFriendsRequestsLoaded();
    }
    public interface onMessagesLoadedListener{
        public abstract void onMessagesLoaded();
    }
}
