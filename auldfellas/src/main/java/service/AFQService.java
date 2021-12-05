package service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import service.core.AbstractQuotationService;
import service.core.ClientInfo;
import service.core.Quotation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the AuldFellas insurance quotation service.
 * 
 * @author Rem
 *
 */
@RestController
public class AFQService extends AbstractQuotationService  {
	// All references are to be prefixed with an AF (e.g. AF001000)
	private static final String PREFIX = "AF";
	private static final String COMPANY = "Auld Fellas Ltd.";
	
	/**
	 * Quote generation:
	 * 30% discount for being male
	 * 2% discount per year over 60
	 * 20% discount for less than 3 penalty points
	 * 50% penalty (i.e. reduction in discount) for more than 60 penalty points 
	 */
	private Map<String, Quotation> quotations = new HashMap<>();

	@RequestMapping(value="/quotations",method= RequestMethod.POST)
	public ResponseEntity<Quotation> createQuotation(@RequestBody ClientInfo info) throws URISyntaxException {
		Quotation quotation = generateQuotation(info);
		quotations.put(quotation.getReference(), quotation);
		String path = ServletUriComponentsBuilder.fromCurrentContextPath().
				build().toUriString()+ "/quotations/"+quotation.getReference();
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(new URI(path));
		return new ResponseEntity<>(quotation, headers, HttpStatus.CREATED);
	}

	@RequestMapping(value="/quotations/{reference}",method=RequestMethod.GET)
	public Quotation getResource(@PathVariable("reference") String reference) {
		Quotation quotation = quotations.get(reference);
		if (quotation == null) throw new NoSuchQuotationException();
		return quotation;
	}

	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public class NoSuchQuotationException extends RuntimeException {
		static final long serialVersionUID = -6516152229878843037L;
	}
	public Quotation generateQuotation(ClientInfo info) {
		// Create an initial quotation between 600 and 1200
		double price = generatePrice(600, 600);

		// Automatic 30% discount for being male
		int discount = (info.getGender() == 'M') ? 30:0;

		// Automatic 2% discount per year over 60...
		discount += (info.getAge() > 60) ? (2*(info.getAge()-60)) : 0;

		// Add a points discount
		discount += getPointsDiscount(info);

		// Generate the quotation and send it back
		return new Quotation(COMPANY, generateReference(PREFIX), (price * (100-discount)) / 100);
	}

	private int getPointsDiscount(ClientInfo info) {
		if (info.getPoints() < 3) return 20;
		if (info.getPoints() <= 6) return 0;
		return -50;

	}

}
