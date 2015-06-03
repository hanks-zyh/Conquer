/*
 * Created by Hanks
 * Copyright (c) 2015 Nashangban. All rights reserved
 *
 */
package app.hanks.com.conquer.presentation;

import org.robobinding.annotation.ItemPresentationModel;
import org.robobinding.annotation.PresentationModel;
import org.robobinding.presentationmodel.HasPresentationModelChangeSupport;
import org.robobinding.presentationmodel.PresentationModelChangeSupport;

import java.util.List;

/**
 * Created by Hanks on 2015/6/3.
 */
@PresentationModel
public class SearchResultPresentationModel implements HasPresentationModelChangeSupport {

    private PresentationModelChangeSupport changeSupport;
    private List<String>                   strings;

    public SearchResultPresentationModel(List<String> list) {
        changeSupport = new PresentationModelChangeSupport(this);
        this.strings = list;
    }

    @ItemPresentationModel(value = StringItemPresentationModel.class)
    public List<String> getStrings() {
        return strings;
    }

    @ItemPresentationModel(value = StringItemPresentationModel.class)
    public void setStrings(List<String> newList) {
        strings.clear();
        strings.addAll(newList);
    }


    @Override
    public PresentationModelChangeSupport getPresentationModelChangeSupport() {
        return changeSupport;
    }
}
