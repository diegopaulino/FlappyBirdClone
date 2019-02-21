package com.cursoandroid.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture[] passaros;
    private Texture fundo;
    private Texture canoBaixo;
    private Texture canoTopo;
    private Texture gameOver;
    private Circle passaroCirculo;
    private Rectangle retanguloCanoTopo;
    private Rectangle retanguloCanoBaixo;
    private BitmapFont fonte;
    private BitmapFont mensagem;

    //Atributos de configuração
    private float larguraDispositivo;
    private float alturaDispositivo;
    private int estadoJogo = 0;
    private int pontuacao = 0;

    private float posicaoPassaro = 120;
    //private ShapeRenderer shape;

    private float variacao = 0;
    private float velocidadeQueda = 0;
    private float posicaoInicialVertical = 0;
    private float posicaoMovimentoCanoHorizontal = 0;
    private float deltaTime = 0;
    private Random numeroRandomico;
    private float alturaEntreCanosRandomico = 0;

    private boolean passou = false;

    //Câmera
    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_WIDTH = 768;
    private final float VIRTUAL_HEIGHT = 1204;

	@Override
	public void create () {

	    batch = new SpriteBatch();

	    numeroRandomico = new Random();
	    fonte = new BitmapFont();
	    fonte.setColor(Color.WHITE);
	    fonte.getData().setScale(6);

	    mensagem = new BitmapFont();
	    mensagem.setColor(Color.WHITE);
	    mensagem.getData().setScale(4);

	    passaros = new Texture[3];
        passaros[0] = new Texture("passaro1.png");
	    passaros[1] = new Texture("passaro2.png");
	    passaros[2] = new Texture("passaro3.png");

	    passaroCirculo = new Circle();
	    retanguloCanoTopo = new Rectangle();
	    retanguloCanoBaixo = new Rectangle();
	    //shape = new ShapeRenderer();

	    fundo = new Texture("fundo.png");
	    canoBaixo = new Texture("cano_baixo.png");
	    canoTopo = new Texture("cano_topo.png");
	    gameOver = new Texture("game_over.png");

	    //Configurações de camera

        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

	    larguraDispositivo = VIRTUAL_WIDTH;
	    alturaDispositivo = VIRTUAL_HEIGHT;
	    posicaoMovimentoCanoHorizontal = larguraDispositivo - 100;

	    posicaoInicialVertical = alturaDispositivo / 2;

	}

	@Override
	public void render () {

        deltaTime = Gdx.graphics.getDeltaTime();
        variacao += Gdx.graphics.getDeltaTime() * 10;
        if (variacao > 2) variacao = 0;

		if(estadoJogo == 0){//Jogo não iniciado

			if(Gdx.input.justTouched()){
				estadoJogo = 1;
			}
		}else {//Jogo iniciado

			velocidadeQueda++;

			if (posicaoInicialVertical >= 0 || velocidadeQueda < 0)
				posicaoInicialVertical -= velocidadeQueda;


            if(estadoJogo == 1){

			    posicaoMovimentoCanoHorizontal -= deltaTime * 200;

                if (Gdx.input.justTouched() && posicaoInicialVertical < alturaDispositivo - 180) {
                    velocidadeQueda = -17;
                }

                //Verifica se o cano saiu por inteiro da tela
                if (posicaoMovimentoCanoHorizontal < -canoTopo.getWidth()) {
                    posicaoMovimentoCanoHorizontal = larguraDispositivo ;
                    alturaEntreCanosRandomico = numeroRandomico.nextInt(400) - 200;
                    passou = false;
                }

                //Verifica pontuação
                if(!passou && posicaoMovimentoCanoHorizontal < posicaoPassaro) {
                    pontuacao++;
                    System.out.println("Adicionou ponto = " + pontuacao);
                    passou = true;
                }
			}else{//Tela game over
                System.out.println("Entrou no else de game over");
                if(Gdx.input.justTouched()) {
                    estadoJogo = 0;
                    pontuacao = 0;
                    velocidadeQueda = 0;
                    posicaoMovimentoCanoHorizontal = larguraDispositivo - 100;
                    posicaoInicialVertical = alturaDispositivo / 2;
                    passou = false;
                }
            }
		}

		//Configurar dados de projeção da câmera
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw( fundo, 0, 0, larguraDispositivo, alturaDispositivo );
        batch.draw(canoTopo, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 + 100 + alturaEntreCanosRandomico);
        batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 - 800 + alturaEntreCanosRandomico);
        batch.draw(passaros[(int) variacao], posicaoPassaro, posicaoInicialVertical );
        fonte.draw(batch, String.valueOf(pontuacao), larguraDispositivo / 2, alturaDispositivo - 50);

        if( estadoJogo == 2 ){
            batch.draw(gameOver, larguraDispositivo / 2 -gameOver.getWidth() / 2, alturaDispositivo / 2);
            mensagem.draw(batch, "Toque para reiniciar", larguraDispositivo / 2 -280 , alturaDispositivo / 2 - gameOver.getHeight() / 2);
        }

        batch.end();

	    passaroCirculo.set(posicaoPassaro + passaros[0].getWidth() / 2, posicaoInicialVertical + passaros[0].getHeight() / 2 , 30);

	    retanguloCanoBaixo = new Rectangle(
	            posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 - 800 + alturaEntreCanosRandomico,
                canoBaixo.getWidth(), canoBaixo.getHeight()
        );

	    retanguloCanoTopo = new Rectangle(
	            posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 + 100 + alturaEntreCanosRandomico,
                canoTopo.getWidth(), canoTopo.getHeight()
        );

        //Teste de colisão

        if( Intersector.overlaps( passaroCirculo, retanguloCanoBaixo ) || Intersector.overlaps( passaroCirculo, retanguloCanoTopo )
                || posicaoInicialVertical <= 0 || posicaoInicialVertical >= alturaDispositivo) {

            estadoJogo = 2;

        }

	    //Desenhar formas
/*
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.circle(passaroCirculo.x, passaroCirculo.y, passaroCirculo.radius);
        shape.rect(retanguloCanoBaixo.x, retanguloCanoBaixo.y, retanguloCanoBaixo.width, retanguloCanoBaixo.height);
        shape.rect(retanguloCanoTopo.x, retanguloCanoTopo.y, retanguloCanoTopo.width, retanguloCanoTopo.height);
        shape.setColor(Color.RED);
        shape.end(); */

            //Teste colisão desenho
            //Gdx.app.log("Colisão", "Houve colisão");


	}

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
