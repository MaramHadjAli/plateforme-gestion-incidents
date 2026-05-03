package tn.enicarthage.plate_be;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.enicarthage.plate_be.dtos.TicketRequestDTO;
import tn.enicarthage.plate_be.dtos.TicketResponseDTO;
import tn.enicarthage.plate_be.entities.*;
import tn.enicarthage.plate_be.repositories.*;
import tn.enicarthage.plate_be.services.EmailService;
import tn.enicarthage.plate_be.services.ScoreCalculatorService;
import tn.enicarthage.plate_be.services.TicketServiceImpl;
import tn.enicarthage.plate_be.converters.TicketConverter;
import tn.enicarthage.plate_be.websocket.NotificationService;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private DemandeurRepository demandeurRepository;

    @Mock
    private TechnicienRepository technicienRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TicketConverter ticketConverter;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ScoreCalculatorService scoreCalculatorService;

    @Mock
    private SalleRepository salleRepository;

    @Mock
    private EquipementRepository equipementRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private Ticket ticket;
    private TicketRequestDTO ticketRequestDTO;
    private Demandeur demandeur;
    private TicketResponseDTO expectedDTO;

    @BeforeEach
    void setUp() {
        demandeur = new Demandeur();
        demandeur.setId(1L);
        demandeur.setEmail("test@enicarthage.tn");
        demandeur.setNom("Test");
        demandeur.setPrenom("User");

        ticket = new Ticket();
        ticket.setIdTicket("TICKET-001");
        ticket.setTitre("Test Ticket");
        ticket.setDescription("Test Description");
        ticket.setStatus(STATUS_TICKET.OUVERT);
        ticket.setDateCreation(new Date());

        ticketRequestDTO = new TicketRequestDTO();
        ticketRequestDTO.setTitre("Test Ticket");
        ticketRequestDTO.setDescription("Test Description");

        expectedDTO = new TicketResponseDTO();
        expectedDTO.setIdTicket("TICKET-001");
        expectedDTO.setTitre("Test Ticket");
    }

    @Test
    void testGetTicketById_Success() {
        when(ticketRepository.findById("TICKET-001")).thenReturn(Optional.of(ticket));
        when(ticketConverter.entityToDto(any(Ticket.class))).thenReturn(expectedDTO);

        TicketResponseDTO foundTicket = ticketService.getTicketById("TICKET-001");

        assertNotNull(foundTicket);
        assertEquals("TICKET-001", foundTicket.getIdTicket());
        verify(ticketRepository, times(1)).findById("TICKET-001");
    }

    @Test
    void testGetTicketById_NotFound() {
        when(ticketRepository.findById("TICKET-999")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ticketService.getTicketById("TICKET-999");
        });

        assertEquals("Ticket non trouvé: TICKET-999", exception.getMessage());
        verify(ticketRepository, times(1)).findById("TICKET-999");
    }

    @Test
    void testGetAllTickets() {
        when(ticketRepository.findAll()).thenReturn(java.util.Collections.singletonList(ticket));
        when(ticketConverter.entityToDto(any(Ticket.class))).thenReturn(expectedDTO);

        var result = ticketService.getAllTickets();

        assertNotNull(result);
        verify(ticketRepository, times(1)).findAll();
    }
}