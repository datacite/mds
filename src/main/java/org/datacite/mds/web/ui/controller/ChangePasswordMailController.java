package org.datacite.mds.web.ui.controller;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.datacite.mds.domain.AllocatorOrDatacentre;
import org.datacite.mds.mail.MailMessage;
import org.datacite.mds.mail.MailMessageFactory;
import org.datacite.mds.service.MailService;
import org.datacite.mds.util.DomainUtils;
import org.datacite.mds.web.ui.model.ChangePasswordMailModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/resources/change_password_mail")
@Controller
public class ChangePasswordMailController {

    Logger log4j = Logger.getLogger(ChangePasswordMailController.class);

    @Autowired
    MailService mailService;

    @Autowired
    MailMessageFactory mailMessageFactory;

    @RequestMapping(method = RequestMethod.GET)
    public String createForm(Model model) {
        model.addAttribute("mail", new ChangePasswordMailModel());
        return "password/mail";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String mail(@ModelAttribute("mail") @Valid ChangePasswordMailModel changePasswordMailModel,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("mail", changePasswordMailModel);
            return "password/mail";
        }

        String symbol = changePasswordMailModel.getSymbol();
        AllocatorOrDatacentre user = DomainUtils.findAllocatorOrDatacentreBySymbol(symbol);
        MailMessage mail = mailMessageFactory.createResetPasswordMail(user);
        mailService.send(mail);
        
        model.addAttribute("symbol", symbol);
        return "password/mail/success";
    }

}
