package service.broker;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import service.core.ClientApplication;
import service.core.ClientInfo;
import service.core.Quotation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the broker service that uses the Service Registry.
 * 
 * @author Rem
 *
 */
@RestController
public class LocalBrokerService{

	private Map<Long, ClientApplication> quotations = new HashMap<>();
	static private long uniqueApplicationNumber = 100;

	@RequestMapping(value="/applications",method= RequestMethod.POST)
	public ResponseEntity<ClientApplication> getQuotations(@RequestBody ClientInfo info) throws URISyntaxException {
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<ClientInfo> request = new HttpEntity<>(info);
		ClientApplication clientApp = new ClientApplication(++uniqueApplicationNumber, info, new ArrayList<Quotation>());

		URI[] testURIs = new URI[3];
		testURIs[0] = new URI("http://auldfellas:8083/quotations");
		testURIs[1] = new URI("http://dodgydrivers:8081/quotations");
		testURIs[2] = new URI("http://girlpower:8082/quotations");

		clientApp.setInfo(info);

		for(URI uri :testURIs){
			Quotation quotation =
					restTemplate.postForObject(uri,
							request, Quotation.class);
			clientApp.addQuotations(quotation);
		}

		quotations.put(uniqueApplicationNumber, clientApp);

		return new ResponseEntity<>(clientApp, new HttpHeaders(), HttpStatus.FOUND);
	}

	@RequestMapping(value="/getApplication",method= RequestMethod.GET)
	public ResponseEntity<ClientApplication> getApplication(@RequestParam long applicationId) throws URISyntaxException {
		if(quotations.containsKey(applicationId)){

			return new ResponseEntity<>(quotations.get(applicationId), new HttpHeaders(), HttpStatus.FOUND);
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@RequestMapping(value="/applications",method= RequestMethod.GET)
	public ResponseEntity<ArrayList<ClientApplication>> getQuotations() throws URISyntaxException {
		if(!quotations.isEmpty())
			return new ResponseEntity<>(new ArrayList<>(quotations.values()), new HttpHeaders(), HttpStatus.FOUND);
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
}