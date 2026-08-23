package com.intellispace.backend.common.security;

import java.lang.annotation.*;

/**
 * Resolves to the authenticated user's id. No resolver is registered for this yet —
 * Step 5's JWT chain provides one. Written now in its final shape so the controllers
 * below don't need to change when Step 5 lands; only a resolver gets added.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUserId {
}