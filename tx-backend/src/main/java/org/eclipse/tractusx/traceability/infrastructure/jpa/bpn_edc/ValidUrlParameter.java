package org.eclipse.tractusx.traceability.infrastructure.jpa.bpn_edc;

import jakarta.validation.Constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = ValidUrlParameterValidator.class)
@Target({ TYPE_USE })
@Retention(RUNTIME)
@Documented
public @interface ValidUrlParameter {

    String message() default "The URL must contain the protocol and a valid domain name.";

}
