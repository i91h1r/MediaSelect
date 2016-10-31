package com.github.hyr0318.mediaselect_library.event;

import com.github.hyr0318.mediaselect_library.ui.Photo;


public interface OnItemCheckListener {

  /***
   *
   * @param position
   * @param path
   *@param isCheck
   * @param selectedItemCount
   * @return enable check
   */
  boolean OnItemCheck(int position, Photo path, boolean isCheck, int selectedItemCount);

}
