%% CARGA Y ANÁLISIS DE DATOS
clear all; close all; clc;

% Cargar CSV
T = readtable('results_part1_complete.csv');

%% Extraer datos únicos
methods = unique(T.Método);
implementations = unique(T.Implementación);

%% Crear figura con subplots (3x3 para 8 métodos)
figure('Position', [100 100 1400 900]);

for methodIdx = 1:length(methods)
    method = methods{methodIdx};
    subplot(3, 3, methodIdx);
    hold on; grid on;
    
    for implIdx = 1:length(implementations)
        impl = implementations{implIdx};
        
        % Filtrar datos
        mask = strcmp(T.Método, method) & strcmp(T.Implementación, impl);
        data = T(mask, :);
        
        if ~isempty(data)
            n = table2array(data(:, 3));
            timeMs = table2array(data(:, 5));
            
            % Graficar en escala log-log
            loglog(n, timeMs, 'o-', 'DisplayName', impl, 'LineWidth', 2, 'MarkerSize', 6);
        end
    end
    
    xlabel('n (tamaño)'); ylabel('Tiempo (ms)');
    title(sprintf('Método: %s', method), 'FontWeight', 'bold');
    legend('Location', 'best', 'FontSize', 8);
    set(gca, 'XScale', 'log', 'YScale', 'log');
end

sgtitle('PARTE 1: Análisis de Métodos List - Big-O Comparison', 'FontSize', 14, 'FontWeight', 'bold');

%% TABLA RESUMEN PARA CADA MÉTODO
fprintf('\n╔════════════════════════════════════════════════════════════════════╗\n');
fprintf('║                 RESUMEN POR MÉTODO                                ║\n');
fprintf('╚════════════════════════════════════════════════════════════════════╝\n\n');

for m = 1:length(methods)
    method = methods{m};
    fprintf('\n### %s ###\n', method);
    fprintf('┌─────────────────────────┬────────────┬────────────┬─────────────┐\n');
    fprintf('│ Implementación          │ n=100      │ n=1M       │ Complejidad │\n');
    fprintf('├─────────────────────────┼────────────┼────────────┼─────────────┤\n');
    
    for impl = 1:length(implementations)
        impl_name = implementations{impl};
        mask = strcmp(T.Método, method) & strcmp(T.Implementación, impl_name);
        data = T(mask, :);
        
        if ~isempty(data)
            times = table2array(data(:, 5));
            ns = table2array(data(:, 3));
            
            t_100 = times(ns == 100);
            t_1m = times(ns == 1000000);
            
            if ~isempty(t_100) && ~isempty(t_1m)
                ratio = t_1m(1) / t_100(1);
                
                if ratio < 2
                    complexity = 'O(1)';
                elseif ratio < 15000
                    complexity = 'O(n)';
                else
                    complexity = 'O(n²)';
                end
                
                fprintf('│ %-23s │ %9.3f │ %9.3f │ %-11s │\n', ...
                    impl_name, t_100(1), t_1m(1), complexity);
            end
        end
    end
    fprintf('└─────────────────────────┴────────────┴────────────┴─────────────┘\n');
end

%% TABLA COMPARATIVA FINAL (4 implementaciones × 8 métodos)
fprintf('\n\n╔════════════════════════════════════════════════════════════════════════════════════════════════╗\n');
fprintf('║                  TABLA COMPARATIVA FINAL (n=1,000,000)                                        ║\n');
fprintf('╚════════════════════════════════════════════════════════════════════════════════════════════════╝\n\n');

% Crear tabla pivote
fprintf('┌──────────────────────────┬─────────────┬─────────────┬──────────────┬──────────────┐\n');
fprintf('│ Método                   │ Singly      │ SinglyWTail │ Doubly       │ DoublyWTail  │\n');
fprintf('├──────────────────────────┼─────────────┼─────────────┼──────────────┼──────────────┤\n');

for m = 1:length(methods)
    method = methods{m};
    fprintf('│ %-24s │', method);
    
    for impl = 1:length(implementations)
        impl_name = implementations{impl};
        mask = strcmp(T.Método, method) & strcmp(T.Implementación, impl_name) & T.n == 1000000;
        data = T(mask, :);
        
        if ~isempty(data)
            timeMs = table2array(data(:, 5));
            fprintf(' %10.3f │', timeMs(1));
        else
            fprintf(' %10s │', 'N/A');
        end
    end
    fprintf('\n');
end

fprintf('└──────────────────────────┴─────────────┴─────────────┴──────────────┴──────────────┘\n');

%% ANÁLISIS DE GANANCIAS DE RENDIMIENTO
fprintf('\n\n╔════════════════════════════════════════════════════════════════════════════════════════════════╗\n');
fprintf('║                  GANANCIAS DE RENDIMIENTO (Doubly vs Singly)                                ║\n');
fprintf('╚════════════════════════════════════════════════════════════════════════════════════════════════╝\n\n');

fprintf('┌──────────────────────────┬─────────────────┐\n');
fprintf('│ Método                   │ Speedup (1M)    │\n');
fprintf('├──────────────────────────┼─────────────────┤\n');

for m = 1:length(methods)
    method = methods{m};
    
    % Singly time
    mask_singly = strcmp(T.Método, method) & strcmp(T.Implementación, 'SinglyLinkedList') & T.n == 1000000;
    data_singly = T(mask_singly, :);
    
    % DoublyWithTail time
    mask_doubly = strcmp(T.Método, method) & strcmp(T.Implementación, 'DoublyLinkedListWithTail') & T.n == 1000000;
    data_doubly = T(mask_doubly, :);
    
    if ~isempty(data_singly) && ~isempty(data_doubly)
        t_singly = table2array(data_singly(:, 5));
        t_doubly = table2array(data_doubly(:, 5));
        speedup = t_singly(1) / t_doubly(1);
        
        fprintf('│ %-24s │ %.1fx faster    │\n', method, speedup);
    end
end

fprintf('└──────────────────────────┴─────────────────┘\n');
