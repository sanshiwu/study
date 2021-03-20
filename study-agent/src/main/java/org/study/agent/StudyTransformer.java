package org.study.agent;

import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.logging.Level;
import java.util.logging.Logger;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;

/**
 * 对类方法进行增强的实现类.
 *
 * @author admin
 */
public final class StudyTransformer implements ClassFileTransformer {

  /** . */
  private static final Logger LOGGER = Logger.getLogger(StudyTransformer.class.getName());

  /**
   * 生成新的class文件,用于调试.
   *
   * @param bytes 加载到内存中的Class字节码数组数据,修改后,重新生存一个.class文件.
   * @param simpleName 源java文件的简单文件名.
   * @author Admin
   */
  private static void generateClass(final byte[] bytes, final String simpleName) {
    // 在target/classes目录下,生成新的.class文件
    final String classPath =
        StudyTransformer.class.getResource("/").getPath() + simpleName + ".class";
    // 自动关闭流,因为实现了Closeable接口.new File(classPath) 可以替换成classPath
    try (final FileOutputStream out = new FileOutputStream(classPath)) {
      out.write(bytes);
    } catch (final Exception e) {
      LOGGER.log(Level.SEVERE, "Generate class Exception: ", e);
    }
  }

  /**
   * 组装代码,将其插入到方法最后一行.
   *
   * @param fqc 类的全限定名.
   * @param methodName 方法名.
   * @param argCount 方法的参数个数.
   * @return 返回组装的代码片段.
   * @author Admin
   */
  private static String assembleCode(
      final String fqc, final String methodName, final int argCount) {
    final String leftBracket = "(";
    final String rightBracket = ")";
    final String plus = "+";
    final String leftMiddleBracket = "[";
    final String rightMiddleBracket = "]";
    final String colon = "::";

    final StringBuilder code = new StringBuilder(500);
    // 打印语句开始
    code.append("System.err.println");
    code.append(leftBracket);
    // 打印内容开始.(使用JDK Date Api会报错,无法增强.具体问题需要再分析,当前系统时间戳)
    code.append("System.currentTimeMillis");
    code.append(leftBracket);
    code.append(rightBracket);
    // 当前线程名 前缀修饰.
    code.append(codeFragment(leftMiddleBracket));
    // 当前线程名内容.
    code.append(plus);
    code.append("java.lang.Thread.currentThread().getName()");
    // 当前线程名 后缀修饰.
    code.append(codeFragment(rightMiddleBracket));
    // 当前增强的类的全限定名和方法名.
    code.append(codeFragment(fqc + colon + methodName + leftBracket));
    // 当前增强的类的方法参数.
    if (0 < argCount) {
      code.append(plus);
      for (int i = 1; i <= argCount; i++) {
        if (1 != i) {
          code.append(codeFragment(","));
          code.append(plus);
        }
        code.append("$");
        code.append(i);
      }
    }
    // 方法结束部分.
    code.append(codeFragment(rightBracket));
    // 方法返回值
    code.append(codeFragment(" Method return: "));
    code.append(plus);
    code.append("$_");
    // 方法执行时间(毫秒)
    code.append(codeFragment(" ,Execute takes "));
    code.append(plus);
    code.append("(endTime - startTime)/(1000L*1000L)");
    code.append(codeFragment("ms"));
    // 打印语句结尾.
    code.append(rightBracket);
    code.append(";");
    return code.toString();
  }

  private static StringBuilder codeFragment(final String codeFragment) {
    final String plus = "+";
    final String marks = "\"";
    final StringBuilder code = new StringBuilder(20);
    code.append(plus);
    code.append(marks);
    code.append(codeFragment);
    code.append(marks);
    return code;
  }

  /**
   * 对单个方法进行增强,在方法的第一行增加开始时间. 在方法的倒数第三行增加结束时间. 倒数第一行用于设置方法的return.
   *
   * @param fqc 类的全限定名.
   * @param method 方法对象.
   * @throws Exception 抛出所有异常,由最终调用者处理.
   * @author Admin
   */
  public static void enhanceMethod(final String fqc, final CtBehavior method) throws Exception {
    // 获取方法的描述符号,不能是native和abstract.
    final int modifiers = method.getModifiers();
    // 排除native方法和抽象方法(无法增强).
    if (!Modifier.isNative(modifiers) && !Modifier.isAbstract(modifiers)) {
      // 获取方法的参数类型.
      final CtClass[] parameterTypes = method.getParameterTypes();
      // 获取参数的个数.
      final int parameterCount = null != parameterTypes ? parameterTypes.length : 0;
      // 给方法的正数第一行增加startTime变量.
      method.addLocalVariable("startTime", CtClass.longType);
      // 给方法的倒数第三行增加endTime变量.
      method.addLocalVariable("endTime", CtClass.longType);
      // startTime变量,赋值当前系统的时间.
      method.insertBefore("startTime = System.nanoTime();");
      // endTime变量,赋值当前系统的时间.
      method.insertAfter("endTime = System.nanoTime(); ");
      // 构建代码片段.
      final String code = assembleCode(fqc, method.getName(), parameterCount);
      // 打印插入的代码详细信息.
      final String flag = "true";
      final String info = System.getProperty("info", flag);
      // true打印.
      if (flag.equals(info)) {
        LOGGER.info(code);
      }
      // 插入到方法的最后二行.
      method.insertAfter(code);
    }
  }

  /**
   * 增强方法的具体实现.
   *
   * <p>插入的代码例子: System.err.println(System.currentTimeMillis()
   * +"["+java.lang.Thread.currentThread().getName()+"]" +
   * "org.study.agent.examples.first.TestMain:main("+$1+")" +"Method return:"+$_+"Execute
   * takes"+(endTime - startTime)/(1000L*1000L)+"ms");
   *
   * @param loader 哪个类加载器,加载的当前内存中的类.
   * @param fqc 内存中的当前类的全限定名.
   * @param clazz 内存中class对象.
   * @param pd 参考父类方法.
   * @param cfb 参考父类方法.
   * @return 增强后的class字节码.
   * @author admin
   */
  @Override
  public byte[] transform(
      final ClassLoader loader,
      final String fqc,
      final Class<?> clazz,
      final ProtectionDomain pd,
      final byte[] cfb) {
    byte[] byteCode = null;
    try {
      // 全限定名默认是/分割的,需要用'.'替换.
      final String fqClass = fqc.replace("/", ".");
      // *代表增强任意方法.
      final String defPackageName = "*";
      // 从系统参数获取-DpackageName=(*/其他)
      final String packageName = System.getProperty("packageName", defPackageName);
      // 对某个包下的类进行处理.如果为空,默认对所有类进行增强,包括JDK中的类.
      if (defPackageName.equals(packageName) || fqClass.startsWith(packageName)) {
        //
        final ClassPool classPool = ClassPool.getDefault();
        //
        final CtClass ctclass = classPool.get(fqClass);
        // 如果当前类是接口,枚举,注解,跳过处理.
        if (!ctclass.isInterface() && !ctclass.isEnum() && !ctclass.isAnnotation()) {
          // 处理所有方法,对每一个方法进行增强处理.
          final CtMethod[] declaredMethods = ctclass.getDeclaredMethods();
          for (final CtMethod method : declaredMethods) {
            enhanceMethod(fqClass, method);
          }
          // 新的字节码数据.
          byteCode = ctclass.toBytecode();
          // 是否生成新的.class文件,用于调试.
          final String generateClass = System.getProperty("generateClass");
          final String flag = "true";
          if (flag.equals(generateClass)) {
            generateClass(byteCode, ctclass.getSimpleName());
          }
          // 防止内存泄露.
          ctclass.detach();
        }
      }
    } catch (final Exception e) {
      LOGGER.log(Level.SEVERE, "Transformer Exception: ", e);
    }
    return byteCode;
  }
}
