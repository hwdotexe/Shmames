package com.hadenwatne.shmames.models;

import java.util.List;

public class PaginatedList {
    private List<String> paginatedList;
    private int itemCount;
    private int pageCursor;

    public PaginatedList(List<String> paginatedList, int itemCount) {
        this.paginatedList = paginatedList;
        this.itemCount = itemCount;
        this.pageCursor = 1;
    }

    public List<String> getPaginatedList() {
        return this.paginatedList;
    }

    public int getItemCount() {
        return this.itemCount;
    }

    public int getPageCursor() {
        return pageCursor;
    }

    public int getNextPage() {
        if (pageCursor < paginatedList.size()) {
            pageCursor++;
            return pageCursor;
        } else {
            pageCursor = 1;
            return 1;
        }
    }

    public int getLastPage() {
        if (pageCursor > 1) {
            pageCursor--;
            return pageCursor;
        } else {
            pageCursor = 1;
            return 1;
        }
    }
}