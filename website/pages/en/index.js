/**
 * Copyright (c) 2017-present, Facebook, Inc.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

const React = require('react');

const CompLibrary = require('../../core/CompLibrary.js');

const MarkdownBlock = CompLibrary.MarkdownBlock; /* Used to read markdown */
const Container = CompLibrary.Container;
const GridBlock = CompLibrary.GridBlock;

class HomeSplash extends React.Component {
  render() {
    const {siteConfig, language = ''} = this.props;
    const {baseUrl, docsUrl} = siteConfig;
    const docsPart = `${docsUrl ? `${docsUrl}/` : ''}`;
    const langPart = `${language ? `${language}/` : ''}`;
    const docUrl = doc => `${baseUrl}${docsPart}${langPart}${doc}`;

    const SplashContainer = props => (
      <div className="homeContainer">
        <div className="homeSplashFade">
          <div className="wrapper homeWrapper">{props.children}</div>
        </div>
      </div>
    );

    const Logo = props => (
      <div className="projectLogo">
        <img src={props.img_src} alt="Project Logo" />
      </div>
    );

    const ProjectTitle = () => (
      <h2 className="projectTitle">
        {siteConfig.title}
        <small>{siteConfig.tagline}</small>
      </h2>
    );

    const PromoSection = props => (
      <div className="section promoSection">
        <div className="promoRow">
          <div className="pluginRowBlock">{props.children}</div>
        </div>
      </div>
    );

    const Button = props => (
      <div className="pluginWrapper buttonWrapper">
        <a className="button" href={props.href} target={props.target}>
          {props.children}
        </a>
      </div>
    );

    return (
      <SplashContainer>
        <Logo img_src={`${baseUrl}img/leaf.svg`} />
        <div className="inner">
          <ProjectTitle siteConfig={siteConfig} />
          <PromoSection>
            <Button href="#try">Try It Out</Button>
            <Button href={docUrl('getting-started.html')}>Read the docs</Button>
          </PromoSection>
        </div>
      </SplashContainer>
    );
  }
}

class Index extends React.Component {
  render() {
    const {config: siteConfig, language = ''} = this.props;
    const {baseUrl} = siteConfig;

    const Block = props => (
      <Container
        padding={['bottom', 'top']}
        id={props.id}
        background={props.background}>
        <GridBlock
          align="center"
          contents={props.children}
          layout={props.layout}
        />
      </Container>
    );

    const FeatureCallout = () => (
      <div
        className="productShowcaseSection paddingBottom"
        style={{textAlign: 'center'}}>
        <h2>Batteries-included web development</h2>
        <MarkdownBlock>Finally the productivity of Ruby on Rails with a type-safe and modern language.</MarkdownBlock>
      </div>
    );

    const TryOut = () => (
      <Container
        padding={['bottom', 'top']}
        id='try'>
        <div style={{textAlign: 'center'}}>
          <h2>
            Try it out
          </h2>
          <p>
            Run the command below from the directory where you want to install Kales, eg.: <code>~/.kales</code>
          </p>
          <iframe
              src="https://carbon.now.sh/embed/?bg=rgba(255%2C255%2C255%2C1)&t=lucario&wt=none&l=application%2Fx-sh&ds=true&dsyoff=20px&dsblur=68px&wc=true&wa=true&pv=56px&ph=56px&ln=false&fm=Hack&fs=14px&lh=133%25&si=false&code=curl%2520https%253A%252F%252Fraw.githubusercontent.com%252Ffelipecsl%252Fkales%252Fmaster%252Fscripts%252Finstall%2520-sSf%2520%257C%2520sh&es=4x&wm=false"
              style={{transform: 'scale(1.1)', width: '1024px', height: '200px', border: '0', overflow: 'hidden'}}
              sandbox="allow-scripts allow-same-origin">
          </iframe>
        </div>
      </Container>
    );
    const docsPart = `${siteConfig.docsUrl ? `${siteConfig.docsUrl}/` : ''}`;
    const langPart = `${language ? `${language}/` : ''}`;
    const docUrl = doc => `${baseUrl}${docsPart}${langPart}${doc}`;
    const gettingStartedUrl = docUrl("getting-started.html");
    const LearnHow = () => (
      <Block background="light">
        {[
          {
            content: '<p> Kales is currently in alpha stability and under active development.' +
                '<br/>Please check out the <a href="' + gettingStartedUrl + '">docs</a> for our official Guides or check back later for more content!</p>',
            imageAlign: 'right',
            title: 'Learn How',
          },
        ]}
      </Block>
    );

    const Features = () => (
      <Block layout="fourColumn">
        {[
          {
            content: 'Kales is built on top of Ktor which is a powerful Kotlin web framework for building asynchronous servers.',
            image: `${baseUrl}img/ktor.png`,
            imageAlign: 'top',
            title: 'Powerful & flexible infrastructure',
          },
          {
              content: 'Connect your models to relational databases using JDBI',
            image: `${baseUrl}img/jdbi.png`,
            imageAlign: 'top',
            title: 'Opinionated with sane defaults',
          },
        ]}
      </Block>
    );

    const Showcase = () => {
      if ((siteConfig.users || []).length === 0) {
        return null;
      }

      const showcase = siteConfig.users
        .filter(user => user.pinned)
        .map(user => (
          <a href={user.infoLink} key={user.infoLink}>
            <img src={user.image} alt={user.caption} title={user.caption} />
          </a>
        ));

      const pageUrl = page => baseUrl + (language ? `${language}/` : '') + page;

      return (
        <div className="productShowcaseSection paddingBottom">
          <h2>Who is using Kales?</h2>
          <p>Add a link to your website here!</p>
          <div className="logos">{showcase}</div>
          <div className="more-users">
            <a className="button" href={pageUrl('users.html')}>
              More {siteConfig.title} Users
            </a>
          </div>
        </div>
      );
    };

    return (
      <div>
        <HomeSplash siteConfig={siteConfig} language={language} />
        <div className="mainContainer">
          <Features />
          <FeatureCallout />
          <TryOut />
          <LearnHow />
          <Showcase />
        </div>
      </div>
    );
  }
}

module.exports = Index;
