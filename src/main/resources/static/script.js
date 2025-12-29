let escolaCnpj = "";

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
            escolaCnpj = escola.cnpjLimpo;

            msg.innerText = `Escola Encontrada: ${escola.nome}`;
            msg.className = "mensagem-status sucesso";

            form.style.display = 'block';
        } else {
            msg.innerText = "Escola não encontrada!";
            msg.className = "mensagem-status erro";
            form.style.display = 'none';
            escolaCnpj = "";
        }
    } catch (error) {
        console.error(error);
        msg.innerText = "Erro ao conectar com servidor.";
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