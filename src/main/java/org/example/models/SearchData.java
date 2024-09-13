package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchData {
  private int  page;
  private int  size ;
  private String  name ;
  private String  category;
  private String  description ;
  private String  sort ;
}
