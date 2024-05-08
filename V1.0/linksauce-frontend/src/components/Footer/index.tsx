import { GithubOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import React from 'react';

const Footer: React.FC = () => {
  const defaultMessage = 'Ohh出品 ';
  const currentYear = new Date().getFullYear();
  return (
    <DefaultFooter
      copyright={
        <>
          {currentYear} {defaultMessage}
          <a href="https://beian.miit.gov.cn/" target="_blank">豫ICP备2024065827号-1  </a>

          <img src="/icons/icon-police.png"/>
          <a href="https://beian.mps.gov.cn/#/query/webSearch" target="_blank">粤公网安备44010602012451号</a>
        </>
      }
      links={[
        {
          key: 'Ant Design Pro',
          title: 'Ant Design Pro',
          href: 'https://pro.ant.design',
          blankTarget: true,
        },
        {
          key: 'github',
          title: <GithubOutlined />,
          href: 'https://github.com/ant-design/ant-design-pro',
          blankTarget: true,
        },
        {
          key: 'Ant Design',
          title: 'Ant Design',
          href: 'https://ant.design',
          blankTarget: true,
        },
      ]}
    />
  );
};

export default Footer;
