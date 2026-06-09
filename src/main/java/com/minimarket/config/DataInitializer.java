package com.minimarket.config;

import com.minimarket.entity.Categoria;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.CategoriaRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.RolRepository;
import com.minimarket.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(RolRepository rolRepository,
                               UsuarioRepository usuarioRepository,
                               CategoriaRepository categoriaRepository,
                               ProductoRepository productoRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            Rol cliente = rolRepository.findByNombre("ROLE_CLIENTE")
                    .orElseGet(() -> rolRepository.save(crearRol("ROLE_CLIENTE")));
            Rol empleado = rolRepository.findByNombre("ROLE_EMPLEADO")
                    .orElseGet(() -> rolRepository.save(crearRol("ROLE_EMPLEADO")));
            Rol admin = rolRepository.findByNombre("ROLE_ADMIN")
                    .orElseGet(() -> rolRepository.save(crearRol("ROLE_ADMIN")));

            crearUsuarioSiNoExiste(usuarioRepository, passwordEncoder, "cliente", "Cliente123", Set.of(cliente));
            crearUsuarioSiNoExiste(usuarioRepository, passwordEncoder, "empleado", "Empleado123", Set.of(empleado));
            crearUsuarioSiNoExiste(usuarioRepository, passwordEncoder, "admin", "Admin123", Set.of(admin));

            if (categoriaRepository.count() == 0) {
                Categoria abarrotes = new Categoria();
                abarrotes.setNombre("Abarrotes");
                categoriaRepository.save(abarrotes);

                Categoria bebidas = new Categoria();
                bebidas.setNombre("Bebidas");
                categoriaRepository.save(bebidas);

                productoRepository.save(crearProducto("Arroz grado 1", 1690.0, 120, abarrotes));
                productoRepository.save(crearProducto("Agua mineral 1.5L", 990.0, 80, bebidas));
            }
        };
    }

    private Rol crearRol(String nombre) {
        Rol rol = new Rol();
        rol.setNombre(nombre);
        return rol;
    }

    private void crearUsuarioSiNoExiste(UsuarioRepository usuarioRepository,
                                        PasswordEncoder passwordEncoder,
                                        String username,
                                        String password,
                                        Set<Rol> roles) {
        if (usuarioRepository.findByUsername(username).isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setUsername(username);
            usuario.setPassword(passwordEncoder.encode(password));
            usuario.setRoles(roles);
            usuarioRepository.save(usuario);
        }
    }

    private Producto crearProducto(String nombre, Double precio, Integer stock, Categoria categoria) {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setCategoria(categoria);
        return producto;
    }
}
