package pl.edu.pw.elka.stud.tkogut.passim

import pl.edu.pw.elka.stud.tkogut.brokering.dialect.{Entity, Dialect, Attribute, AttributeType}


/**
 * User:  Tomasz Kogut
 * Email: tomasz.kogut [at] hotmail.com
 * Date:  24.06.12
 * Time:  20:58
 *
 */

  object PassimDialect extends Dialect("PassimDialect") {

    final val UNIVERSITY = new Entity("University")
    final val universityName =  Attribute("UniversityName", AttributeType.STRING);
    final val universityFoundationYear =  Attribute("FoundationYear", AttributeType.DATE_TIME);
    final val universityCountry =  Attribute("HomeCity", AttributeType.STRING);
    final val universityHomeCity =  Attribute("HomeCity", AttributeType.ADDRESS);
    final val universityHomepage =  Attribute("UniversityHomepage", AttributeType.URL);
    final val universityDepartments =  Attribute("Departamens", AttributeType.STRING, true);

    final val universityAttributesList = List(universityName, universityFoundationYear,
      universityHomeCity,universityCountry, universityDepartments)
    UNIVERSITY.addAttributes(universityAttributesList)


    final val PUBLICATION = new Entity("Publication")
    final val publicationAuthor = Attribute("PersonName", AttributeType.NAME,true);
    final val publicationTitle = Attribute("PublicationTitle", AttributeType.STRING);
    final val publicationISBN = Attribute("ISBN", AttributeType.STRING);
    final val publicationArea = Attribute("PublicationArea", AttributeType.STRING, true)
    final val publicationYear = Attribute("PublicationYear", AttributeType.DATE_TIME);
    final val publicationIsCitedBy = Attribute("PublicationCited", AttributeType.STRING, true)
    final val publicationCitationsNumber = Attribute("PublicationCitationNumber", AttributeType.INTEGER);

    final val WEBSITE = new Entity("Website")
    final val websiteDate = Attribute("WebsiteDate",AttributeType.DATE_TIME)
    final val websiteUrl = Attribute("WebsiteUrl",AttributeType.URL)
    final val websiteTitle = Attribute("WebsiteTitle",AttributeType.STRING)
    final val websiteBrief = Attribute("WebsiteBrief",AttributeType.STRING)
    
    final val websiteAttributesList = List(websiteDate,websiteUrl,websiteTitle,websiteBrief)
    WEBSITE.addAttributes(websiteAttributesList)

    final val publicationAttributesList = List(publicationCitationsNumber, publicationIsCitedBy, publicationYear,
      publicationArea, publicationISBN, publicationTitle, publicationAuthor)
    PUBLICATION.addAttributes(publicationAttributesList)

    final val PERSON = new Entity("Person")
    final val personName = publicationAuthor;
    final val personYearsOld = Attribute("PersonYearsOld", AttributeType.INTEGER);
    final val personPhoneNumber = Attribute("PersonPhoneNumber", AttributeType.PHONE_NUMBER);
    final val personEmail = Attribute("PersonEmail", AttributeType.EMAIL)
    final val personHomepage = websiteUrl
    final val personGrad = Attribute("PersonGrad", AttributeType.GRAD)
    final val personHomeUniversity = universityName
    final val personHIndex = Attribute("PersonHIndex", AttributeType.INTEGER);
    final val personPicture = Attribute("PersonPicture", AttributeType.URL)
    final val personCitationNum = Attribute("PersonCitationNumber", AttributeType.INTEGER)

    final val personAttributesList = List(personName, personYearsOld,
      personPhoneNumber, personEmail, personHomepage, personGrad, personHomeUniversity, personHIndex,
      personCitationNum, personPicture)

    PERSON.addAttributes(personAttributesList)

    
    addEntity(UNIVERSITY)
    addEntity(PUBLICATION) 
    addEntity(PERSON)
    addEntity(WEBSITE)
    
    //val allEntities = university :: publication :: person :: Nil



  }
