package com.github.StormTeam.Storm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Comment {
    String value();

    CommentLocation location() default CommentLocation.INLINE;

    public enum CommentLocation {
        INLINE(1),
        TOP(2),
        BOTTOM(3);
        private int id;

        CommentLocation(int location) {
            this.id = location;
        }

        public int getID() {
            return id;
        }
    }
}

