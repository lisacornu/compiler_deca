import matplotlib.pyplot as plt
cycles = [0,0,2,6,4,6,10,8,6,8,10,14,14,16,12,10,8,10,12,16,14,16,20,18,18,20,22,26,16,18,14,12,10,12,14,18,16,18,22,20,18,20,22,26,26,28,24,22,22,24,26,30,28,30,34,32,20,22,24,28,18,20,16,14,12,14,16,20,18,20,24,22,20,22,24,28,28,30,26,24,22,24,26,30,28,30,34,32,32,34,36,40,30,32,28,26,26,28,30,34]

x = list(range(len(cycles)))  # Indices des éléments

# Création de la figure
plt.figure(figsize=(10, 6))

# Tracé des données
plt.plot(x, cycles, label="Cycles",linewidth=3, linestyle="-", color="blue")

# Ligne horizontale à y=20
plt.axhline(y=20, color="red", label="clock cycles de MUL")


for power in [2, 4, 8, 16, 32, 64]:
    plt.axvline(x=power, color="green", linestyle="-", linewidth=1, alpha=0.7, label="$x=2^n$" if power == 2 else None)

# Ajout des labels et du titre
plt.xlabel("entier i")
plt.ylabel("Clock cycles")
plt.title("Clock cycles des multiplication de Booth sur une architecture IMA")

# Affichage de la légende
plt.legend()

# Affichage du graphique
plt.show()
