package ru.rksp.suslov.dto;

import java.time.LocalDateTime;

public class EventDto {
    private String идентификатор;
    private String номерЗаказа;
    private String номерТелефонаПокупателя;
    private String описаниеЗаказа;
    private LocalDateTime датаСобытия;

    public String getИдентификатор() {
        return идентификатор;
    }

    public void setИдентификатор(String идентификатор) {
        this.идентификатор = идентификатор;
    }

    public String getНомерЗаказа() {
        return номерЗаказа;
    }

    public void setНомерЗаказа(String номерЗаказа) {
        this.номерЗаказа = номерЗаказа;
    }

    public String getНомерТелефонаПокупателя() {
        return номерТелефонаПокупателя;
    }

    public void setНомерТелефонаПокупателя(String номерТелефонаПокупателя) {
        this.номерТелефонаПокупателя = номерТелефонаПокупателя;
    }

    public String getОписаниеЗаказа() {
        return описаниеЗаказа;
    }

    public void setОписаниеЗаказа(String описаниеЗаказа) {
        this.описаниеЗаказа = описаниеЗаказа;
    }

    public LocalDateTime getДатаСобытия() {
        return датаСобытия;
    }

    public void setДатаСобытия(LocalDateTime датаСобытия) {
        this.датаСобытия = датаСобытия;
    }
}
