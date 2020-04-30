/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import snet.NetworkHook;
import sutilities.BasicUtils;
import sutilities.Debugger;

@SuppressWarnings("unchecked")
public class ReflectionUtils {
  
  /**
   * Instantiates all classes that fulfills the specified conditions, and returns 
   * them in a list:
   * <ul>
   *  <li>The class must be located in the specified package.</li>
   *  <li>The class must be a subclass of the specified class.</li>
   * </ul>
   * The constructor parameter types and arguments may be specified.
   * @param packagePath The target package
   * @param forcedSuperclass A class that all targets must extend
   * @param paramTypes The parameter types of the method
   * @param arguments The values to use as arguments
   */
  public static <E> List<NetworkHook<?>> runMethodForAllClassesInPackage(String packagePath,
      Class<E> forcedSuperclass, Class<?>[] paramTypes, Object... arguments) {
    
    String sourcePath = getSourcePath().replace("\\", "/");
    boolean isInJar = isInJar(sourcePath);
    
    if (isInJar)
      return registerPackageJar(sourcePath, packagePath, forcedSuperclass, paramTypes, arguments);
    else
      return registerPackage(sourcePath, packagePath, forcedSuperclass, paramTypes, arguments);
  }


  /**
   * Returns the path to this file (as a binary).
   * @return The path to this file (as a binary)
   */
  private static String getSourcePath()
  {
    try
    {
      return URLDecoder.decode(ReflectionUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
    }
    catch (IOException exception)
    {
      Debugger.warning(ReflectionUtils.class.getSimpleName() + ": getSourcePath()", "Failed to decode the code source!", exception);
    }
    
    return null;
  }
  
  
  /**
   * Returns whether or not the binaries of this program are located within a jar
   *  file.
   * @param sourcePath The path to this source file (as a binary)
   * @return Whether or not the binaries of this program are located within a jar
   *  file
   */
  private static boolean isInJar(String sourcePath)
  {
    if (sourcePath.toLowerCase().endsWith(".jar") || sourcePath.toLowerCase().endsWith(".exe"))
    {
      return true;
    }
    else
    {
      return false;
    }
  }


  private static <E> List<E> registerPackage(String basePath, String packagePath,
      Class<?> forcedSuperclass, Class<?>[] paramTypes,
      Object... arguments)
  {
    List<E> objects = new ArrayList<E>();
    
    File packageFolder = new File(basePath + packagePath);

    File[] filesInPackage = packageFolder.listFiles();
    for (File currentFile : filesInPackage)
    {
      String path = currentFile.getAbsolutePath().replace("\\", "/");
      if (!path.startsWith("/"))
        path = "/" + path;
      E result = (E) doReflection(packagePath, forcedSuperclass, paramTypes, path.substring(basePath.length()),
          arguments);
      if (result != null)
        objects.add(result);
    }
    
    return objects;
  }
  
  
  private static <E> List<E> registerPackageJar(String jarPath, String packagePath,
      Class<?> forcedSuperclass, Class<?>[] paramTypes,
      Object... arguments)
  {
    List<E> objects = new ArrayList<E>();
    
    JarFile jarFile = null;
    try
    {
      jarFile = new JarFile(jarPath);
      Enumeration<JarEntry> entries = jarFile.entries();
      
      while (entries.hasMoreElements())
      {
        String path = entries.nextElement().getName().replace("\\", "/");;
        E result = (E) doReflection(packagePath, forcedSuperclass, paramTypes, path,
            arguments);
        if (result != null)
          objects.add(result);
      }
    }
    catch (IOException exception)
    {
      Debugger.warning(ReflectionUtils.class.getSimpleName() + ": registerPackage()", "Failed to find or open jar file " + exception.getMessage(), exception);
    }
    
    BasicUtils.closeSilently(jarFile);
    
    return objects;
  }

  
  private static <E> E doReflection(String packagePath, Class<E> forcedSuperclass,
      Class<?>[] paramTypes, String path, Object... objects) {

    if (path.endsWith(".class")) {
      path = path.replace(".class", "");

      String fileName = null;
      try {
        if (path.toLowerCase().startsWith(packagePath.toLowerCase()))
        {
          Class<?> fileClass = Class.forName(path.replace('/', '.'));

          fileName = fileClass.getSimpleName();

          if (fileClass.getModifiers() == Modifier.PUBLIC &&
              extendsClass(fileClass, forcedSuperclass))
            return (E) fileClass.getDeclaredConstructor(paramTypes).newInstance(objects);
        }
      }
      catch (ClassNotFoundException e) {
        Debugger.warning(ReflectionUtils.class.getSimpleName() + ": registerPackage()", "Missing class when processing " + fileName, e);
      }
      catch (IllegalAccessException e) {
        Debugger.warning(ReflectionUtils.class.getSimpleName() + ": registerPackage()", "The constructor with the specified parameters is not public in " + fileName, e);
      }
      catch (InvocationTargetException e) {
        Debugger.warning(ReflectionUtils.class.getSimpleName() + ": registerPackage()", "An exception was thrown when instantiating " + fileName, e);
      }
      catch (NoSuchMethodException e) {
        Debugger.warning(ReflectionUtils.class.getSimpleName() + ": registerPackage()", "The constructor with the specified parameters is not declared for " + fileName, e);
      }
      catch (InstantiationException e) {
        Debugger.warning(ReflectionUtils.class.getSimpleName() + ": registerPackage()", "Cannot instantiate an abstract class, " + fileName, e);
      } 
      catch (IllegalArgumentException e) {
        Debugger.warning(ReflectionUtils.class.getSimpleName() + ": registerPackage()", "The constructor parameter list and the argument list does not match for " + fileName, e);
      }
    }
    
    return null;
  }
  
  
  private static boolean extendsClass(Class<?> classToCheck, Class<?> superClass)
  {
    Class<?> sc = classToCheck.getSuperclass();
    
    if (superClass.getName().equals(Object.class.getName()))
      return true;
    
    while (!sc.getName().equals(Object.class.getName()))
    {
      if (sc.getName().equals(superClass.getName()))
        return true;
      
      sc = sc.getSuperclass();
    }
    
    return false;
  }
}
