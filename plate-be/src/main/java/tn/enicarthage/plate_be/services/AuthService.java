package tn.enicarthage.plate_be.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import tn.enicarthage.plate_be.dtos.auth.LoginRequest;
import tn.enicarthage.plate_be.dtos.auth.RegisterRequest;
import tn.enicarthage.plate_be.entities.*;
import tn.enicarthage.plate_be.repositories.UserRepository;
import tn.enicarthage.plate_be.security.JwtUtil;


@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public Utilisateur register(RegisterRequest request) {

        if (userRepository.findByEmail(request.email).isPresent()) {
            throw new RuntimeException("Email déjà utilisé");
        }

        Utilisateur user = new Utilisateur();
        user.setNom(request.nom);
        user.setEmail(request.email);
        user.setPassword(passwordEncoder.encode(request.password));
        user.setRole(ROLE.DEMANDEUR);

        return userRepository.save(user);
    }

    public String login(LoginRequest request) {

        Utilisateur user = userRepository.findByEmail(request.email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtUtil.generateToken(user.getEmail());
    }
}