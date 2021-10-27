import fr.inria.corese.core.Graph;
import fr.inria.corese.core.load.Load;
import fr.inria.corese.core.load.LoadException;
import fr.inria.corese.core.print.*;
import fr.inria.corese.core.query.QueryEngine;
import fr.inria.corese.core.query.QueryProcess;
import fr.inria.corese.kgram.core.Mapping;
import fr.inria.corese.kgram.core.Mappings;
import fr.inria.corese.sparql.api.IDatatype;
import fr.inria.corese.sparql.exceptions.EngineException;
import fr.inria.corese.sparql.triple.parser.NSManager;
import net.sf.saxon.lib.SaxonOutputKeys;

import javax.script.ScriptEngineFactory;
import java.util.HashMap;
import java.util.Map;

public class Application {
    public static void main(String[] args) throws EngineException, LoadException {
        String RESOURCES_PATH = "src/main/resources/" ;

        Map prefixes = new HashMap<String,String>();
        prefixes.put("rdf","@prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> .");
        prefixes.put("ns1","@prefix ns1:<http://www.inria.fr/2015/humans#> .");
        prefixes.put("ns1-instances","@prefix ns1-instances: <http://www.inria.fr/2015/humans-instances#>.");

        Graph graph = Graph.create(true);
        Load ld = Load.create(graph);
        //ld.load(RESOURCES_PATH + "human1.rdf"); // load method is deprecated use parse !
        ld.parse(RESOURCES_PATH + "human1.rdf"); // this causes LoadException;


        // -----------------------------------------QUERIES ------------------------------------------------------------------------------
        // retrieve all the db
        String query = "select *  where {?x ?p ?y}"; // select + ResultFormat == XML FORMAT !!!
        String querybis = "construct where {?x ?p ?y}"; // construct + ResultFormat == RDF FORMAT !!!!

        // retrieve all individuals who have rdf:type
        String query1 = "select ?x ?y where {?x rdf:type ?y }";
        //retrive individuals that are Lecturer
        String query3 = prefixes.get("rdf")+""+prefixes.get("ns1")+"select ?x where {?x rdf:type ns1:Lecturer}" ;
        //retrieve individuals that are Researcher
        String query4 = prefixes.get("rdf")+""+prefixes.get("ns1")+"select ?x where {?x rdf:type ns1:Researcher}" ;
        // retrieve all Men
        String query5 = prefixes.get("rdf")+""+prefixes.get("ns1")+"select ?x where {?x rdf:type ns1:Man}" ;
        //Spouse of harry
        String query6 = prefixes.get("rdf")+""+prefixes.get("ns1")+"select ?x ?y  where {?x ns1:name 'Harry'. ?x ns1:hasSpouse ?y}";
        String query6bis = prefixes.get("rdf")+""+prefixes.get("ns1-instances")+"select ?z where {ns1-instances:Harry ns1:hasSpouse ?z}";
        //to retreive the properties of a person we need to use it's URI, we can't use rdf:ID because the constructed graph won't contain an rdf:Id property therefore : 'rdf:ID ns1-instac:Harry' won't work!
        String query7 = prefixes.get("rdf")+""+prefixes.get("ns1-instances")+"select ?y ?z where {ns1-instances:Harry ?y ?z}";
        //approximative search !
        String approximateQuery = "SELECT MORE  * WHERE {?x ?y  ?z}";
        //template
        String queryTemplate = "template {'subject : ' ?x ', property: '?p',value: ' ?y} where {?x ?p ?y}";
        //add data, N.B : the result isn't written in human.rdf, its added to the graph we're querying therefore to see the effect we need to run another query to print the graph
        String addQuery = prefixes.get("ns1")+""+prefixes.get("ns1-instances")+"insert data {ns1-instances:Jim a ns1:Man;ns1:age 18 .ns1-instances:Jill a ns1:Woman;ns1:age 81}";
        String addPropQuery = prefixes.get("ns1")+""+prefixes.get("ns1-instances")+"insert {ns1-instances:Jim ns1:name 'Jim'} where {}";
        String addPropQuery1 = prefixes.get("ns1")+""+prefixes.get("ns1-instances")+"insert {ns1-instances:Jim ns1:hasSpouse ns1-instances:Jill} where {}";
        //SQL request, usefull to merge different databases
        String sqlQuery = "select  * where { select ( sql('select emp,name from Employee where age >= 18') as (?emp, ?name) ) where {} }";
       //multi graph request
        String graph1="humnans1";
        String graph2="humnans2";

        String multiQuery =prefixes.get("nsa1")+" SELECT ?x ?name FROM <humans1>. FROM <humans2>. WHERE  { ?x ns1:name ?name }";
        //---------------------------------------------------------------- Processing----------------------------------------------
        QueryProcess exec = QueryProcess.create(graph);
        Mappings map  = exec.query(query); // this causes the Engine Exception
       // Mappings map2 = exec.query(addPropQuery);
      //  Mappings map3 = exec.query(addPropQuery1);
      //  Mappings map1 = exec.query(querybis);
       // Mappings map1  = exec.sparqlQuery(approximateQuery);

        // nice way of retreiving values of specific properties !
        for (Mapping m :map){
            IDatatype dt = (IDatatype) m.getValue("?x");
            // System.out.println( dt.stringValue()); // print the URIS of each individual
            //  dt.getDatatypeURI() ;// printed null , try it on another RDF !
            //  dt.intValue();
            //dt.doubleValue();
            //dt.booleanValue();

        }

        // ---------------------------------------------------------------PRINTING ------------------------------------------------

       //  ResultFormat f1 = ResultFormat.create(map); System.out.println(f1); //to print the result of the query
        // ResultFormat f2 = ResultFormat.create(map1); System.out.println(f2);//
        //printing in a pretty RDF way ! with select DESIGN but won't work !


        // ResultFormat f2 = ResultFormat.create(map1);System.out.println(f2);

        //TripleFormat f2 = TripleFormat.create(graph);System.out.println(f2); // pour afficher nos donnée
       //System.out.println(CSVFormat.create(map)); // to return the result in CSV
       System.out.println(RDFFormat.create(graph)); // print our graph in RDF FORMAT, don't work with result map
       //System.out.println(RDFResultFormat.create(map));



        //------------------------------------------------------RULES ----------------------------------

        /*

        ld.loadWE("data.rul");
        RuleEngine re = ld.getRuleEngine();
        re.process();
        g.addEngine(re);
        g.process();

         */



        //todo
        // sql
        //read template presentation
        // do the multi graph querying
        //try to check path functionnality
        //sequencing
    }
}
