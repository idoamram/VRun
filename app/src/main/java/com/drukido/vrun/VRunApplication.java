package com.drukido.vrun;

import android.app.Application;
import android.content.res.Configuration;

import com.drukido.vrun.entities.Group;
import com.drukido.vrun.entities.GroupEvent;
import com.drukido.vrun.entities.Run;
import com.drukido.vrun.entities.User;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

public class VRunApplication extends Application{

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {

        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(GroupEvent.class);
        ParseObject.registerSubclass(Group.class);
        ParseObject.registerSubclass(Run.class);
        Parse.initialize(this, "nN26DMCJZ7odYFgUfWiC6POvnSkYLyvi9EXBaDsd", "FmsncvA2mUvQO4q0ocQxJ0mwonjr5itOX9qQR7DO");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
