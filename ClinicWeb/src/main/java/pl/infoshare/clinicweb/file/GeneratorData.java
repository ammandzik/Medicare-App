package pl.infoshare.clinicweb.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import io.github.viepovsky.polishutils.pesel.Pesel;
import io.github.viepovsky.polishutils.pesel.PeselGenerator;
import io.github.viepovsky.polishutils.pesel.PeselGeneratorParams;
import org.springframework.ui.Model;
import pl.infoshare.clinicweb.doctor.Doctor;
import pl.infoshare.clinicweb.doctor.Specialization;
import pl.infoshare.clinicweb.patient.Address;
import pl.infoshare.clinicweb.patient.Patient;
import pl.infoshare.clinicweb.user.Gender;
import pl.infoshare.clinicweb.user.PersonDetails;

import java.util.*;

public class GeneratorData {


    private static final int count = 15;

    static String PATIENT_PATH = "ClinicWeb/src/main/resources/patients.json";
    static String DOCTOR_PATH = "ClinicWeb/src/main/resources/doctors.json";

    static Specialization[] specializations = Specialization.values();
    static List<Specialization> list = Arrays.stream(specializations).toList();

    static Random rand = new Random();
    static Faker faker = new Faker(new Locale("pl"));
    static PeselGeneratorParams.Gender gender = PeselGeneratorParams.Gender.FEMALE;

    public static PersonDetails generatePersonDetails() {
        PersonDetails personDetails = new PersonDetails();
        Gender gender = faker.options().option(Gender.FEMALE, Gender.MALE);
        String generatedPesel = PeselGenerator.generatePeselStatic();
        Pesel pesel = new Pesel(generatedPesel);
        personDetails.setPesel(generatedPesel);
        personDetails.setName(faker.name().firstName());
        personDetails.setSurname(faker.name().lastName());
        personDetails.setPhoneNumber(faker.phoneNumber().phoneNumber());
        personDetails.setBirthDate(pesel.getBirthDate());
        personDetails.setGender(gender);
        return personDetails;
    }

    private static Address generateAddress() {
        Address address = new Address();
        address.setCity(faker.address().city());
        address.setCountry(faker.address().country());
        address.setZipCode(faker.address().zipCode());
        address.setStreet(faker.address().streetAddress());
        address.setHouseNumber(faker.address().buildingNumber());
        address.setFlatNumber(faker.address().streetAddressNumber());
        return address;
    }

    private static void writeRandomObjects(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        FileService fileService = new FileService(mapper);
        for (int i = 0; i < GeneratorData.count; i++) {
            if (object instanceof Patient) {
                Object o = generateRandomPatient(object);
                fileService.writeToFile(o, PATIENT_PATH);
            } else if (object instanceof Doctor) {
                Object doctor = generateRandomDoctor(object);
                fileService.writeToFile(doctor, DOCTOR_PATH);
            }

        }

    }

    private static Patient generateRandomPatient(Object object) {
        Patient patient = new Patient();
        if (object instanceof Patient) {
            patient.setPersonDetails(generatePersonDetails());
            patient.setAddress(generateAddress());
        } else throw new NoSuchElementException();
        return patient;
    }


    private static Doctor generateRandomDoctor(Object o) {
        Doctor doctor = new Doctor();
        if (o instanceof Doctor) {
            Specialization specialization = list.get(rand.nextInt(list.size()));
            doctor.setPersonDetails(generatePersonDetails());
            doctor.setAddress(generateAddress());
            doctor.setSpecialization(specialization.getDescription());
        } else throw new NoSuchElementException();
        return doctor;

    }

    public static void generateAndSaveData() {
        writeRandomObjects(new Patient());
        writeRandomObjects(new Doctor());
    }

    public static void generateAndSaveData(Model model) {
        Patient patient = new Patient();
        Doctor doctor = new Doctor();
        writeRandomObjects(patient);
        writeRandomObjects(doctor);
        model.addAttribute("patient", patient);
        model.addAttribute("doctor", doctor);
    }
}