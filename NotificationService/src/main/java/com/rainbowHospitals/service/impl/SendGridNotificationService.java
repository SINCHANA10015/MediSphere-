package com.rainbowHospitals.service.impl;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.rainbowHospitals.dto.EmailRequest;
import com.rainbowHospitals.rainbowHospitals.config.SendGridProperties;
import com.rainbowHospitals.service.SendEmailService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

@Service
public class SendGridNotificationService implements SendEmailService {

	private final SendGridProperties sendGridProperties;

	public SendGridNotificationService(SendGridProperties sendGridProperties) {
		this.sendGridProperties = sendGridProperties;
	}

	@Override
	public void sendEmail(EmailRequest requestDto) {
		Email from = new Email(sendGridProperties.getFromEmail(), sendGridProperties.getFromName());
		Email to = new Email(requestDto.getTo());
		Content content = new Content("text/html", requestDto.getBody());

		Mail mail = new Mail(from, requestDto.getSubject(), to, content);

		SendGrid sendGrid = new SendGrid(sendGridProperties.getApiKey());

		Request request = new Request();
		try {
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());
			
			Response response = new Response();
			 if (response.getStatusCode() != 202) {
	                String errorMsg = "Failed to send email. Status Code: " + response.getStatusCode() + ", Body: " + response.getBody();
	                System.err.println(errorMsg);
	                throw new RuntimeException(errorMsg);
	            }  } catch (IOException e) {
	                System.err.println("=== SendGrid IOException ===");
	                System.err.println("Error Message: " + e.getMessage());
	                e.printStackTrace();
	                throw new RuntimeException("SendGrid Error: " + e.getMessage(), e);
	            } catch (Exception e) {
	                System.err.println("=== Unexpected Exception in SendGrid ===");
	                System.err.println("Error Message: " + e.getMessage());
	                e.printStackTrace();
	                throw new RuntimeException("Unexpected error sending email: " + e.getMessage(), e);
	            }
			
		}
	}


