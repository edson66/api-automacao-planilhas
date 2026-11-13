let escolaCnpj = "";
let escolaSelecionada = null;

function definirEscolaSelecionada(escola) {
    escolaSelecionada = escola;
    escolaCnpj = escola ? escola.cnpjLimpo : "";
    document.getElementById('btnEditarEscola').style.display = escola ? 'block' : 'none';
}

function toggleDiv(idDiv, mostrar) {
    const div = document.getElementById(idDiv);
    div.style.display = mostrar ? 'block' : 'none';
}

async function verificarEscola() {
    const cnpj = document.getElementById('inputCnpj').value;
    const msg = document.getElementById('resultadoEscola');
    const form = document.getElementById('formGeracao');

    if(!cnpj) { alert("Digite o CNPJ!"); return; }

    try {
        const response = await fetch(`/escolas/buscar?cnpj=${cnpj}`);

        if (response.ok) {
            const escola = await response.json();
            definirEscolaSelecionada(escola);

            msg.innerText = `Escola Encontrada: ${escola.nome}`;
            msg.className = "mensagem-status sucesso";

            form.style.display = 'block';
        } else {
            msg.innerText = "Escola não encontrada!";
            msg.className = "mensagem-status erro";
            form.style.display = 'none';
            definirEscolaSelecionada(null);
        }
    } catch (error) {
        console.error(error);
        msg.innerText = "Erro ao conectar com servidor.";
    }
}

let debounceBuscaNome = null;

function buscarPorNome() {
    const input = document.getElementById('inputBuscaNome');
    const lista = document.getElementById('sugestoesNome');
    const nome = input.value.trim();

    clearTimeout(debounceBuscaNome);

    if (nome.length < 2) {
        lista.innerHTML = '';
        lista.style.display = 'none';
        return;
    }

    debounceBuscaNome = setTimeout(async () => {
        try {
            const response = await fetch(`/escolas/buscar-por-nome?nome=${encodeURIComponent(nome)}`);
            if (!response.ok) { return; }

            const escolas = await response.json();
            lista.innerHTML = '';

            if (escolas.length === 0) {
                lista.style.display = 'none';
                return;
            }

            escolas.forEach(escola => {
                const item = document.createElement('div');
                item.className = 'sugestao-item';
                item.innerText = `${escola.nome} (${escola.cnpj})`;
                item.onclick = () => selecionarEscolaSugerida(escola);
                lista.appendChild(item);
            });

            lista.style.display = 'block';
        } catch (error) {
            console.error(error);
        }
    }, 300);
}

function selecionarEscolaSugerida(escola) {
    const lista = document.getElementById('sugestoesNome');
    const msg = document.getElementById('resultadoEscola');
    const form = document.getElementById('formGeracao');

    document.getElementById('inputBuscaNome').value = escola.nome;
    document.getElementById('inputCnpj').value = escola.cnpjLimpo;
    lista.innerHTML = '';
    lista.style.display = 'none';

    definirEscolaSelecionada(escola);
    msg.innerText = `Escola Encontrada: ${escola.nome}`;
    msg.className = "mensagem-status sucesso";
    form.style.display = 'block';
}

document.addEventListener('click', (event) => {
    const lista = document.getElementById('sugestoesNome');
    const input = document.getElementById('inputBuscaNome');
    if (lista && event.target !== input && !lista.contains(event.target)) {
        lista.style.display = 'none';
    }
});

function abrirModalCadastro(event) {
    event.preventDefault();
    document.getElementById('modalCadastro').style.display = 'flex';
}

function fecharModalCadastro() {
    document.getElementById('modalCadastro').style.display = 'none';
    document.getElementById('resultadoCadastro').innerText = '';
}

async function cadastrarEscola() {
    const cnpjDigitado = document.getElementById('cadNovoCnpj').value;
    const nome = document.getElementById('cadNovoNome').value;
    const cep = document.getElementById('cadNovoCep').value;
    const cidade = document.getElementById('cadNovoCidade').value;
    const endereco = document.getElementById('cadNovoEndereco').value;
    const diretor = document.getElementById('cadNovoDiretor').value;
    const msg = document.getElementById('resultadoCadastro');

    const cnpjLimpo = cnpjDigitado.replace(/\D/g, '');

    if (!cnpjLimpo || !nome || !cep || !cidade || !endereco) {
        msg.innerText = "Preencha todos os campos obrigatórios!";
        msg.className = "mensagem-status erro";
        return;
    }

    try {
        const response = await fetch('/escolas', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                cnpjLimpo: cnpjLimpo,
                cnpj: cnpjDigitado,
                nome: nome,
                cep: cep,
                cidade: cidade,
                endereco: endereco,
                diretor: diretor
            })
        });

        if (response.ok) {
            msg.innerText = "Escola cadastrada com sucesso!";
            msg.className = "mensagem-status sucesso";
            setTimeout(() => {
                fecharModalCadastro();
                document.getElementById('cadNovoCnpj').value = '';
                document.getElementById('cadNovoNome').value = '';
                document.getElementById('cadNovoCep').value = '';
                document.getElementById('cadNovoCidade').value = '';
                document.getElementById('cadNovoEndereco').value = '';
                document.getElementById('cadNovoDiretor').value = '';
            }, 1000);
        } else {
            msg.innerText = "Erro ao cadastrar. Verifique os dados.";
            msg.className = "mensagem-status erro";
        }
    } catch (error) {
        console.error(error);
        msg.innerText = "Erro ao conectar com servidor.";
        msg.className = "mensagem-status erro";
    }
}

function abrirModalEdicao() {
    if (!escolaSelecionada) { return; }

    document.getElementById('editCnpj').value = escolaSelecionada.cnpj || '';
    document.getElementById('editNome').value = escolaSelecionada.nome || '';
    document.getElementById('editCep').value = escolaSelecionada.cep || '';
    document.getElementById('editCidade').value = escolaSelecionada.cidade || '';
    document.getElementById('editEndereco').value = escolaSelecionada.endereco || '';
    document.getElementById('editDiretor').value = escolaSelecionada.diretor || '';
    document.getElementById('resultadoEdicao').innerText = '';

    document.getElementById('modalEdicao').style.display = 'flex';
}

function fecharModalEdicao() {
    document.getElementById('modalEdicao').style.display = 'none';
    document.getElementById('resultadoEdicao').innerText = '';
}

async function salvarEdicaoEscola() {
    if (!escolaSelecionada) { return; }

    const cnpjDigitado = document.getElementById('editCnpj').value;
    const nome = document.getElementById('editNome').value;
    const cep = document.getElementById('editCep').value;
    const cidade = document.getElementById('editCidade').value;
    const endereco = document.getElementById('editEndereco').value;
    const diretor = document.getElementById('editDiretor').value;
    const msg = document.getElementById('resultadoEdicao');

    const cnpjLimpo = cnpjDigitado.replace(/\D/g, '');

    if (!cnpjLimpo || !nome || !cep || !cidade || !endereco) {
        msg.innerText = "Preencha todos os campos obrigatórios!";
        msg.className = "mensagem-status erro";
        return;
    }

    try {
        const response = await fetch(`/escolas/${escolaSelecionada.id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                cnpjLimpo: cnpjLimpo,
                cnpj: cnpjDigitado,
                nome: nome,
                cep: cep,
                cidade: cidade,
                endereco: endereco,
                diretor: diretor
            })
        });

        if (response.ok) {
            const escolaAtualizada = await response.json();
            definirEscolaSelecionada(escolaAtualizada);

            document.getElementById('inputCnpj').value = escolaAtualizada.cnpjLimpo;
            document.getElementById('inputBuscaNome').value = escolaAtualizada.nome;
            const status = document.getElementById('resultadoEscola');
            status.innerText = `Escola Encontrada: ${escolaAtualizada.nome}`;
            status.className = "mensagem-status sucesso";

            msg.innerText = "Escola atualizada com sucesso!";
            msg.className = "mensagem-status sucesso";
            setTimeout(fecharModalEdicao, 1000);
        } else {
            msg.innerText = "Erro ao atualizar. Verifique os dados.";
            msg.className = "mensagem-status erro";
        }
    } catch (error) {
        console.error(error);
        msg.innerText = "Erro ao conectar com servidor.";
        msg.className = "mensagem-status erro";
    }
}

async function enviarTudo() {
    const arquivoInput = document.getElementById('inputArquivo');
    if(arquivoInput.files.length === 0) {
        alert("Selecione o arquivo Excel!");
        return;
    }

    if(!document.getElementById('inputNf').value) {
        alert("Preencha o número da NF!");
        return;
    }

    if(!document.getElementById('dataOrcamentos').value) {
        alert("Preencha a data dos orçamentos!");
        return;
    }

    const formData = new FormData();
    formData.append("cnpj", escolaCnpj);
    formData.append("nf", document.getElementById('inputNf').value);
    formData.append("dataOrcamentos", document.getElementById('dataOrcamentos').value);
    formData.append("arquivoDoador", arquivoInput.files[0]);

    const temRecibo = document.getElementById('chkRecibo').checked;
    const temConsolidacao = document.getElementById('chkConsolidacao').checked;

    formData.append("temRecibo", temRecibo);
    formData.append("temConsolidacao", temConsolidacao);

    if (temRecibo) {
        const dataRecibo = document.getElementById('dataRecibo').value;
        if(!dataRecibo) { alert("Data do recibo é obrigatória!"); return; }
        formData.append("dataRecibo", dataRecibo);

        formData.append("pagoPorMeioDe", document.getElementById('pagoPor').value);
        const dataNotaValor = document.getElementById('dataNota').value;
         if (dataNotaValor){
            formData.append("dataNota", dataNotaValor);
         }
    }

    if (temConsolidacao) {
        const dataCons = document.getElementById('dataConsolidacao').value;
        if(!dataCons) { alert("Data da consolidação é obrigatória!"); return; }
        formData.append("dataConsolidacao", dataCons);
    }

    try {
        const btn = document.querySelector('.btn-gerar');
        btn.innerText = "Gerando...";
        btn.disabled = true;

        const response = await fetch('/documentos/gerar', {
            method: 'POST',
            body: formData
        });

        if (response.ok) {
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `documentos_${document.getElementById('inputNf').value}.zip`;
            document.body.appendChild(a);
            a.click();
            a.remove();
            alert("Documentos gerados com sucesso!");
        } else {
            alert("Erro ao gerar documentos. Verifique os dados.");
        }
    } catch (error) {
        console.error(error);
        alert("Erro na conexão.");
    } finally {
        const btn = document.querySelector('.btn-gerar');
        btn.innerText = "GERAR DOCUMENTOS";
        btn.disabled = false;
    }
}