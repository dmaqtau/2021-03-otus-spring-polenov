package ru.otus.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.spring.util.Const;

@Service
@RequiredArgsConstructor
public class AboutAppServiceImpl implements AboutAppService {
    private final LocaleService localeService;

    @Override
    public String getAboutAppMessage() {
        // Сообщение - информация о текущем приложении
        return localeService.getLocalizedString(Const.STRINGS_ABOUT);
    }
}
