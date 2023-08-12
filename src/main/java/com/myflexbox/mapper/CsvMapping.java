package com.myflexbox.mapper;

import com.myflexbox.entity.Address;
import com.myflexbox.entity.User;
import lombok.Getter;

import java.util.function.BiConsumer;

@Getter
public class CsvMapping {
    private final String csvColumnName;
    private final BiConsumer<User, String> userSetter;
    private final BiConsumer<Address, String> addressSetter;

    public CsvMapping(String csvColumnName, BiConsumer<User, String> userSetter, BiConsumer<Address, String> addressSetter) {
        this.csvColumnName = csvColumnName;
        this.userSetter = userSetter;
        this.addressSetter = addressSetter;
    }

    public void applyToUser(User user, String value) {
        if (userSetter != null) {
            userSetter.accept(user, value);
        }
    }

    public void applyToAddress(Address address, String value) {
        if (addressSetter != null) {
            addressSetter.accept(address, value);
        }
    }

    @Override
    public String toString() {
        return csvColumnName;
    }
}
