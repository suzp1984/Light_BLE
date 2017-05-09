package org.zpcat.ble.injector;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/*
  Just to show how to use Qualifiers to identify a dependency.
  In this case, there are no need to identify one, since the context
  are only from the ApplicationModule.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface ApplicationContext {
}
