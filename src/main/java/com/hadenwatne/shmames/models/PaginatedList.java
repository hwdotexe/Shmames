package com.hadenwatne.shmames.models;

import java.util.List;

public class PaginatedList {
    private List<String> paginatedList;
    private int itemCount;

    public PaginatedList(List<String> paginatedList, int itemCount) {
        this.paginatedList = paginatedList;
        this.itemCount = itemCount;
    }

    public List<String> getPaginatedList() {
        return this.paginatedList;
    }

    public int getItemCount() {
        return this.itemCount;
    }
}
