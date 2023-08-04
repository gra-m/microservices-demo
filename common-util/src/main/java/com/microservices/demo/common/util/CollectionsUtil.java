package com.microservices.demo.common.util;

import java.util.ArrayList;
import java.util.List;
/**
 * Instead of having this as a component and requiring spring dependency => set up a Singleton in a
 * thread-safe manner
 */
public class CollectionsUtil {

  private CollectionsUtil() {}

  public static CollectionsUtil getInstance() {
    return CollectionsUtilInstanceHolder.INSTANCE;
  }

  public <T> List<T> getListFromIterable(Iterable<T> iterable) {
    List<T> list = new ArrayList<>();
    iterable.forEach(t -> list.add(t));
    return list;
  }

  private static class CollectionsUtilInstanceHolder {
    static final CollectionsUtil INSTANCE = new CollectionsUtil();
  }
}
