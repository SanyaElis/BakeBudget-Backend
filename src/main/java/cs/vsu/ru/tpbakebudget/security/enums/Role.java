package cs.vsu.ru.tpbakebudget.security.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_USER, ROLE_ADVANCED_USER;

    @Override
    public String getAuthority() {
        return name();
    }
}
