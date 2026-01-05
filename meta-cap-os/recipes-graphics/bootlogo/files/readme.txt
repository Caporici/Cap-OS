🖥️ Bootlogo no Linux (Yocto) – Do erro à solução
🎯 Objetivo

Exibir uma imagem (bootlogo / splash screen) diretamente no framebuffer do Linux, sem X11, Wayland ou compositor — igual ao que vemos em celulares quando ligam.

1️⃣ O que é “bootlogo” nesse contexto?

Existem 3 níveis diferentes de bootlogo:

Estágio	Quem desenha	Observação
Boot ROM	Hardware	Logo do SoC (fixo)
U-Boot	Bootloader	Splash antes do kernel
Linux framebuffer	Kernel	Logo simples, sem GUI

👉 Aqui estamos falando do terceiro:
escrever diretamente no framebuffer do kernel Linux (/dev/fbX).

2️⃣ Primeira tentativa: “jogar uma imagem no framebuffer”

A ideia básica é simples:

cat imagem.raw > /dev/fbX


Mas isso só funciona se:

✔ resolução correta
✔ formato de pixel correto
✔ tamanho exato
✔ framebuffer certo

Sem isso → imagem quebrada, repetida, cor errada ou erro de escrita.

3️⃣ Descobrindo qual framebuffer usar

Listamos os framebuffers disponíveis:

ls /dev/fb*


No nosso caso:

/dev/fb0
/dev/fb1


Testamos:

cat imagem > /dev/fb0  ❌
cat imagem > /dev/fb1  ✅


👉 Conclusão: o display está ligado ao fb1, não ao fb0.

4️⃣ Sintomas do erro (importante pro vídeo)

Quando jogamos uma imagem errada no framebuffer, vimos:

fundo azul

imagem repetida várias vezes

imagem “estourando” a tela

erro No space left on device

⚠️ Isso NÃO é bug aleatório
É sintoma clássico de desalinhamento de framebuffer.

5️⃣ Diagnóstico correto: perguntar ao kernel

Aqui está o ponto-chave do vídeo.

📌 Nunca assuma resolução
📌 Pergunte ao driver

Rodamos:

fbset -i


Resultado:

mode "640x480"
geometry 640 480 640 480 16
LineLength 1280


⚠️ Isso enganou — porque o DRM reporta um modo genérico.

A informação real veio daqui 👇

6️⃣ A fonte da verdade: sysfs do framebuffer
cat /sys/class/graphics/fb1/virtual_size
cat /sys/class/graphics/fb1/bits_per_pixel
cat /sys/class/graphics/fb1/stride


Resultado:

virtual_size    = 480,320
bits_per_pixel  = 16
stride          = 960

7️⃣ Interpretando isso como engenheiro
Resolução real
480 × 320 pixels

Formato
16 bpp → RGB565

Stride
960 bytes por linha
480 × 2 bytes = 960 ✔


👉 Framebuffer linear, sem padding

8️⃣ Calculando o tamanho máximo do framebuffer

Isso aqui é FUNDAMENTAL:

480 × 320 × 2 bytes = 307.200 bytes ≈ 300 KB


📌 Esse é o tamanho máximo que pode ser escrito em /dev/fb1

9️⃣ Entendendo o erro “No space left on device”

Nós tentamos escrever:

imagem de 600 KB (640×480×2)

Mas o framebuffer comporta:

300 KB

Resultado:

write() → ENOSPC


👉 Kernel bloqueou corretamente a escrita.

🔟 Gerando a imagem correta (RGB565)

Usamos ffmpeg para gerar a imagem exatamente no formato do framebuffer:

ffmpeg -y \
  -i bootlogo.png \
  -vf scale=480:320 \
  -pix_fmt rgb565le \
  -f rawvideo bootlogo.rgb


Verificação:

ls -lh bootlogo.rgb


Resultado esperado:

≈ 300K

1️⃣1️⃣ Escrevendo corretamente no framebuffer

Forma correta (mais segura que cat):

dd if=bootlogo.rgb of=/dev/fb1 bs=4096 status=progress


✔ imagem inteira
✔ sem repetição
✔ sem cor errada
✔ sem erro de escrita

🎉 Bootlogo exibido com sucesso

1️⃣2️⃣ Por que a resolução “não bate” com o display físico?

Isso é ouro pro vídeo.

Mesmo que o display seja fisicamente:

640×480


O driver pode exportar:

framebuffer rotacionado

framebuffer recortado

framebuffer escalado

framebuffer lógico (portrait vs landscape)

👉 Quem manda é o driver + device tree, não o painel.

🧠 Lições finais (fecha o vídeo bem)

Framebuffer é memória crua, sem metadados

Linux não faz conversão automática

Resolução, formato e tamanho precisam bater bit a bit

Sysfs é a fonte da verdade

Debug de bootlogo é debug de driver, não de imagem

🎬 Próximos vídeos possíveis (gancho)

Você pode puxar fácil:

Splash screen no U-Boot

De onde vem o framebuffer no Device Tree

Diferença entre fbdev e DRM

Por que /dev/fb0 existe mas não mostra nada

Como desenhar direto no framebuffer em C

Se quiser, no próximo passo eu:

transformo isso num roteiro de vídeo

ou escrevo o script do vídeo inteiro

ou monto um diagrama de boot

Agora você tem conteúdo de engenheiro, não de tutorial fraco.