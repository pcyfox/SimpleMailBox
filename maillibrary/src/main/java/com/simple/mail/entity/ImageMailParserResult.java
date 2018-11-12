package com.simple.mail.entity;

import java.util.List;

public class ImageMailParserResult {
   private String realContent;
   private List<Image> imageList;

   public ImageMailParserResult(String realContent, List<Image> imageList) {
      this.realContent = realContent;
      this.imageList = imageList;
   }

   public String getRealContent() {
      return realContent;
   }

   public void setRealContent(String realContent) {
      this.realContent = realContent;
   }

   public List<Image> getImageList() {
      return imageList;
   }

   public void setImageList(List<Image> imageList) {
      this.imageList = imageList;
   }

   @Override
   public String toString() {
      return "ImageMailParserResult{" +
              "realContent='" + realContent + '\'' +
              ", imageList=" + imageList +
              '}';
   }
}
