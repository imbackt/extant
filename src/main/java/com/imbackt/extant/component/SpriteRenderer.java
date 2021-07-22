package com.imbackt.extant.component;

import com.imbackt.extant.Component;

public class SpriteRenderer extends Component {

    private Boolean firstTime = false;

    @Override
    public void start() {
        System.out.println("I am starting");
    }

    @Override
    public void update(float dt) {
        if (!firstTime) {
            System.out.println("I am updating");
            firstTime = true;
        }
    }
}
