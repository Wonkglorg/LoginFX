package com.wonkglorg.loginfx.pages.editor;

import com.wonkglorg.fxutility.manager.ManagedController;
import com.wonkglorg.loginfx.objects.UserData;

public class EditorController extends ManagedController {

    private UserData user;
    @Override
    public void update() {

    }


    //todo create editor, add logging info, and a panel for all fields which can be changed with a button, reuse register panel for that

    public void setUser(UserData user) {
        this.user = user;
    }
}
