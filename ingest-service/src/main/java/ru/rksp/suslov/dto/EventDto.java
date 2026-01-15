package ru.rksp.suslov.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EventDto {
    @JsonProperty("идентификатор")
    @JsonAlias({"идентификатор"})
    private String идентификатор;
    
    @JsonProperty("номерЗаказа")
    @JsonAlias({"номерЗаказа", "номерзаказа"})
    private String номерЗаказа;
    
    @JsonProperty("номерТелефонаПокупателя")
    @JsonAlias({"номерТелефонаПокупателя", "номертелефонапокупателя", "номер телефонаПокупателя"})
    private String номерТелефонаПокупателя;
    
    @JsonProperty("описаниеЗаказа")
    @JsonAlias({"описаниеЗаказа", "описаниезаказа"})
    private String описаниеЗаказа;
    
    @JsonProperty("датаСобытия")
    @JsonAlias({"датаСобытия", "датасобытия"})
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
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
