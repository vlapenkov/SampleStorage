package com.yst.sklad.tsd.data;

/**
 * Created by lapenkov on 23.01.2019.
 */

public class DeleteItemDto  {
    public DeleteItemDto (Long item, boolean deleteAll)
    {
        mItem=item;
        mDeleteAll=deleteAll;
    }
   public Long mItem;
   public boolean mDeleteAll;
}