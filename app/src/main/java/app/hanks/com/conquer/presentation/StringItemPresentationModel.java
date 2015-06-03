/*
 * Created by Hanks
 * Copyright (c) 2015 Nashangban. All rights reserved
 *
 */
package app.hanks.com.conquer.presentation;

import org.robobinding.itempresentationmodel.ItemContext;
import org.robobinding.itempresentationmodel.ItemPresentationModel;

/**
 * Created by Hanks on 2015/6/3.
 */
public class StringItemPresentationModel implements ItemPresentationModel<String> {
    private String value;

    @Override
    public void updateData(String bean, ItemContext itemContext) {
        value = bean;
    }

    public String getValue() {
        return value;
    }
}
