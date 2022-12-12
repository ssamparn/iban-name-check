package com.sparkle.demo.ibannamecheckasyncimpl.service.iban;

import com.sparkle.demo.ibannamecheckasyncimpl.document.IbanEntity;
import com.sparkle.demo.ibannamecheckasyncimpl.repository.IbanNameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IBanNameCheckService {

    private final IbanNameRepository ibanNameRepository;

    public void insertIban(String iban) {

        IbanEntity entity = new IbanEntity();
        entity.setIban("iban");

        ibanNameRepository.save(entity);

    }
}
